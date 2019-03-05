package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_history_detail.*
import kotlinx.android.synthetic.main.list_item_slaughter_detail.view.*

import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.HistoryMortalityDialogFragment
import my.com.engpeng.epslaughterhouse.model.SlaughterDetail
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject

class TripHistoryDetailFragment : Fragment() {

    private val appDb: AppDb by inject()

    private var slaughterId: Long = 0

    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_trip_history_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.trip_history_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_print -> {
                findNavController().navigate(TripHistoryDetailFragmentDirections.actionTripHistoryDetailFragmentToTripPrintFragment(slaughterId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        slaughterId = TripHistoryDetailFragmentArgs.fromBundle(arguments!!).slaughterId

        appDb.slaughterDao().getDpById(slaughterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            it.run {
                                et_company.setText(companyName)
                                et_location.setText(locationName)
                                et_doc_date.setText(docDate)
                                et_doc_no.setText("${docType}-${docNo}")
                                et_type.setText(type)
                                et_truck_code.setText(truckCode)
                            }
                        }, {}
                ).addTo(compositeDisposable)

        appDb.slaughterDetailDao().getAllBySlaughterId(slaughterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = DetailDialogAdapter(it)
                    }
                }
                .addTo(compositeDisposable)

        appDb.slaughterDetailDao().getTtlBySlaughterId(slaughterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    tv_ttl_weight.text = it.ttlWeight.format2Decimal()
                    tv_ttl_qty.text = it.ttlQty.toString()
                    tv_ttl_cage.text = it.ttlCage.toString()
                    tv_ttl_cover.text = it.ttlCover.toString()
                }
                .addTo(compositeDisposable)

        btn_mortality.setOnClickListener {
            appDb.slaughterMortalityDao().getAllBySlaughterId(slaughterId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        HistoryMortalityDialogFragment.show(fragmentManager!!, it)
                    }.addTo(compositeDisposable)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}

class DetailDialogAdapter(private val detailList: List<SlaughterDetail>)
    : RecyclerView.Adapter<DetailDialogAdapter.DetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_slaughter_detail, parent, false))
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        detailList[position].let { detail ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = detail.weight.format2Decimal()
                li_tv_qty.text = detail.qty.toString()
                li_tv_cage.text = detail.cage.toString()
                li_tv_cover.text = detail.cover.toString()

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}