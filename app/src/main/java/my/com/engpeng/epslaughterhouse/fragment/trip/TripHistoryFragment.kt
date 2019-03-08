package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_history.*
import kotlinx.android.synthetic.main.list_item_trip_history.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.model.SlaughterDisplay
import my.com.engpeng.epslaughterhouse.util.formatYesNo
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class TripHistoryFragment : Fragment() {

    private val appDb: AppDb by inject()

    private var rvAdapter = TripHistoryAdapter()

    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_trip_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = rvAdapter.apply {
            clickEvent.subscribeOn(Schedulers.io())
                    .delay(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { slaughter ->
                        findNavController().navigate(TripHistoryFragmentDirections.actionTripHistoryFragmentToTripHistoryDetailFragment(slaughter.id!!))
                    }.addTo(compositeDisposable)
        }

        appDb.slaughterDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}

class TripHistoryAdapter : RecyclerView.Adapter<TripHistoryAdapter.TripViewHolder>() {

    private val clickSubject = PublishSubject.create<Slaughter>()
    val clickEvent: Observable<Slaughter> = clickSubject

    private var slaughterList: List<SlaughterDisplay>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_trip_history, parent, false))
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        slaughterList!![position].let { slaughter ->
            holder.itemView.run {
                clicks().map { slaughter }.subscribe(clickSubject)
                li_tv_no.text = (itemCount - position).toString()
                li_tv_company.text = slaughter.companyCode
                li_tv_location.text = slaughter.locationCode
                li_tv_doc_date.text = slaughter.docDate
                li_tv_doc_no.text = "${slaughter.docType}-${slaughter.docNo}"
                li_tv_type.text = slaughter.type
                li_tv_upload.text = slaughter.isUpload?.formatYesNo()

                if (slaughter.isDelete == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed50))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return slaughterList?.size ?: 0
    }

    fun setList(slaughterList: List<SlaughterDisplay>) {
        this.slaughterList = slaughterList
        this.notifyDataSetChanged()
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}