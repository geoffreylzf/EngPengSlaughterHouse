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
import kotlinx.android.synthetic.main.fragment_trip_history.*
import kotlinx.android.synthetic.main.list_item_trip_history.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.TripDisplay
import my.com.engpeng.epslaughterhouse.util.formatYesNo
import org.koin.android.ext.android.inject

class TripHistoryFragment : Fragment() {

    private val appDb: AppDb by inject()

    private lateinit var rvAdapter: TripHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_trip_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)

        rvAdapter = TripHistoryAdapter(object : TripHistoryAdapter.Listener {
            override fun onClicked(tripId: Long) {
                findNavController().navigate(TripHistoryFragmentDirections.actionTripHistoryFragmentToTripHistoryDetailFragment(tripId))
            }
        })

        rv.adapter = rvAdapter

        appDb.tripDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class TripHistoryAdapter(val listener: Listener) : RecyclerView.Adapter<TripHistoryAdapter.TripViewHolder>() {

    interface Listener {
        fun onClicked(tripId: Long)
    }

    private var tripList: List<TripDisplay>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_trip_history, parent, false))
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        tripList!![position].let { trip ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_company.text = trip.companyCode
                li_tv_location.text = trip.locationCode
                li_tv_doc_date.text = trip.docDate
                li_tv_doc_no.text = "${trip.docType}-${trip.docNo}"
                li_tv_type.text = trip.type
                li_tv_upload.text = trip.isUpload?.formatYesNo()

                if (trip.isDelete == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed50))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }

                setOnClickListener {
                    listener.onClicked(trip.id!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tripList?.size ?: 0
    }

    fun setList(slaughterList: List<TripDisplay>) {
        this.tripList = slaughterList
        this.notifyDataSetChanged()
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}