package my.com.engpeng.epslaughterhouse.fragment.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location
import java.util.*

class TripHeadViewModel(private val appDb: AppDb)
    : ViewModel() {

    private var compositeDisposable = CompositeDisposable()

    val liveCompany = MutableLiveData<Company?>()
    val liveLocation = MutableLiveData<Location?>()
    val liveCalendar = MutableLiveData<Calendar>()

    val liveIsQrScan = MutableLiveData<Boolean>()

    init {
        liveCalendar.value = Calendar.getInstance()
        liveIsQrScan.value = false
    }

    fun setIsQrScan(b: Boolean) {
        liveIsQrScan.value = b
    }

    fun loadCompany(companyId: Long) {
        appDb.companyDao().getById(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveCompany.value = it
                }, {
                    liveCompany.value = null
                }).addTo(compositeDisposable)
    }

    fun loadLocation(locationId: Long) {
        appDb.locationDao().getById(locationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveLocation.value = it
                }, {
                    liveLocation.value = null
                }).addTo(compositeDisposable)
    }

    fun setCalendar(calendar: Calendar){
        liveCalendar.value = calendar
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}