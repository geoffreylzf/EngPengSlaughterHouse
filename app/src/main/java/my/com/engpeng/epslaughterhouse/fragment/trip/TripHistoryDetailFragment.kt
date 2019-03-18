package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_trip_history_detail.*
import kotlinx.android.synthetic.main.list_item_slaughter_detail.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.HistoryMortalityDialogFragment
import my.com.engpeng.epslaughterhouse.model.TripDetail
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject

class TripHistoryDetailFragment : Fragment() {

    private val appDb: AppDb by inject()

    private var tripId: Long = 0
    private var menu: Menu? = null

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
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_print -> {
                findNavController().navigate(TripHistoryDetailFragmentDirections.actionTripHistoryDetailFragmentToTripPrintFragment(tripId))
                true
            }
            R.id.mi_delete -> {
                delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        tripId = TripHistoryDetailFragmentArgs.fromBundle(arguments!!).slaughterId

        CoroutineScope(Dispatchers.IO).launch {
            val trip = appDb.tripDao().getDpByIdAsync(tripId)
            withContext(Dispatchers.Main) {
                trip.run {
                    et_company.setText(companyName)
                    et_location.setText(locationName)
                    et_doc_date.setText(docDate)
                    et_doc_no.setText("${docType}-${docNo}")
                    et_type.setText(type)
                    et_truck_code.setText(truckCode)
                    if (isUpload == 0 && isDelete == 0) {
                        menu?.findItem(R.id.mi_delete)?.isVisible = true
                    }
                }
            }

            val detail = appDb.tripDetailDao().getAllByTripIdAsync(tripId)
            withContext(Dispatchers.Main) {
                if (detail.isNotEmpty()) {
                    rv.layoutManager = LinearLayoutManager(context)
                    rv.adapter = DetailDialogAdapter(detail)
                }
            }

            val tripTtl = appDb.tripDetailDao().getTtlByTripId(tripId)
            withContext(Dispatchers.Main) {
                tv_ttl_weight.text = tripTtl.ttlWeight.format2Decimal()
                tv_ttl_qty.text = tripTtl.ttlQty.toString()
                tv_ttl_cage.text = tripTtl.ttlCage.toString()
                tv_ttl_cover.text = tripTtl.ttlCover.toString()
            }
        }

        btn_mortality.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val mortalityList = appDb.tripMortalityDao().getAllByTripIdAsync(tripId)
                withContext(Dispatchers.Main) {
                    HistoryMortalityDialogFragment.show(fragmentManager!!, mortalityList)
                }
            }
        }
    }

    private fun delete() {
        ConfirmDialogFragment.show(fragmentManager!!,
                getString(R.string.dialog_title_delete_trip),
                getString(R.string.dialog_confirm_msg_delete_trip),
                getString(R.string.delete), object : ConfirmDialogFragment.Listener {
            override fun onPositiveButtonClicked() {

                CoroutineScope(Dispatchers.IO).launch {
                    val trip = appDb.tripDao().getByIdAsync(tripId)
                    appDb.tripDao().insertAsync(trip.apply { isDelete = 1 })
                    withContext(Dispatchers.Main) {
                        AlertDialogFragment.show(fragmentManager!!, getString(R.string.success), getString(R.string.dialog_success_delete))
                    }
                }
            }

            override fun onNegativeButtonClicked() {}
        })
    }
}

class DetailDialogAdapter(private val detailList: List<TripDetail>)
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