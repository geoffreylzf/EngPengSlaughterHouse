package my.com.engpeng.epslaughterhouse.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_item_location.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.Location

class LocationDialogAdapter(private val locationList: List<Location>)
    : RecyclerView.Adapter<LocationDialogAdapter.LocationViewHolder>() {

    private val clickSubject = PublishSubject.create<Location>()
    val clickEvent: Observable<Location> = clickSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_location, parent, false))

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        locationList[position].let { location ->
            holder.itemView.apply {
                clicks().map { location }.subscribe(clickSubject)
            }.run {
                li_tv_location_code.text = location.locationCode
                li_tv_location_name.text = location.locationName
            }
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}