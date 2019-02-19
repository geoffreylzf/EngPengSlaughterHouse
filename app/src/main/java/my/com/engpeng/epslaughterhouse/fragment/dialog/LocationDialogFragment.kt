package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_location.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.LocationDialogAdapter
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
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null
    private var companyId: Long? = null

    private val selectSubject = PublishSubject.create<Location>()
    val selectEvent: Observable<Location> = selectSubject

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        disposable = appDb.locationDao().getAllByCompanyId(companyId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        //rv.height = dialog.hei
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = LocationDialogAdapter(it).apply {
                            disposable2 = clickEvent
                                    .subscribeOn(Schedulers.io())
                                    .delay(200, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { location ->
                                        selectSubject.onNext(location)
                                        dismiss()
                                    }
                        }
                    } else {
                        tv_title.setText(R.string.dialog_title_no_company)
                    }
                }

        return inflater.inflate(R.layout.fragment_dialog_location, container, false)
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
        disposable2?.dispose()
    }

}