package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_location.*
import kotlinx.android.synthetic.main.list_item_location.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.model.Location
import java.util.concurrent.TimeUnit

class LocationDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun getInstance(fm: FragmentManager, companyId: Long): LocationDialogFragment {
            return LocationDialogFragment().apply {
                this.companyId = companyId
                show(fm, TAG)
            }
        }
    }

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    private var companyId: Long? = null
    private var compositeDisposable = CompositeDisposable()

    private val selectSubject = PublishSubject.create<Location>()
    val selectEvent: Observable<Location> = selectSubject

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_location, container, false)
    }

    override fun onResume() {
        super.onResume()
        appDb.locationDao().getAllByCompanyId(companyId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        //rv.height = dialog.hei
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = LocationDialogAdapter(it).apply {
                            clickEvent.subscribeOn(Schedulers.io())
                                    .delay(200, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { location ->
                                        selectSubject.onNext(location)
                                        selectSubject.onComplete()
                                        dismiss()
                                    }.addTo(compositeDisposable)
                        }
                    } else {
                        tv_title.setText(R.string.dialog_title_no_company)
                    }
                }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}

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