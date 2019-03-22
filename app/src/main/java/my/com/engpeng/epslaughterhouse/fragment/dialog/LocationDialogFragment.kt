package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dialog_location.*
import kotlinx.android.synthetic.main.list_item_location.view.*
import kotlinx.coroutines.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Location
import org.koin.android.ext.android.inject

class LocationDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun show(fm: FragmentManager, companyId: Long, listener: Listener) {
            return LocationDialogFragment().apply {
                this.companyId = companyId
                this.listener = listener
            }.show(fm, TAG)
        }
    }

    interface Listener {
        fun onSelected(locationId: Long)
    }

    private val appDb: AppDb by inject()
    private lateinit var listener: Listener
    private var companyId: Long? = null


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            if(companyId != null){
                val locationList = appDb.locationDao().getAllByCompanyId(companyId!!)
                withContext(Dispatchers.Main) {
                    if (locationList.isNotEmpty()) {
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = LocationDialogAdapter(locationList, object : LocationDialogAdapter.Listener {
                            override fun onClicked(locationId: Long) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    delay(200)
                                    withContext(Dispatchers.Main) {
                                        listener.onSelected(locationId)
                                        dismiss()
                                    }
                                }
                            }
                        })
                    } else {
                        tv_title.setText(R.string.dialog_title_no_location)
                    }
                }
            }
        }
    }

}

class LocationDialogAdapter(
        private val locationList: List<Location>,
        private val listener: Listener)
    : RecyclerView.Adapter<LocationDialogAdapter.LocationViewHolder>() {

    interface Listener {
        fun onClicked(locationId: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_location, parent, false))

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        locationList[position].let { location ->
            holder.itemView.run {
                li_tv_location_code.text = location.locationCode
                li_tv_location_name.text = location.locationName

                setOnClickListener {
                    listener.onClicked(location.id!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}