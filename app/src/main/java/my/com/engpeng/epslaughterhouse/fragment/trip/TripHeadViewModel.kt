package my.com.engpeng.epslaughterhouse.fragment.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location

class TripHeadViewModel
    : ViewModel() {

    val liveCompany = MutableLiveData<Company>()
    val liveLocationId = MutableLiveData<Location>()
    val liveIsQrScan = MutableLiveData<Boolean>()

    init {
        liveIsQrScan.value = false
    }

    fun setIsQrScan(b: Boolean) {
        liveIsQrScan.value = b
    }

    override fun onCleared() {
        super.onCleared()
    }
}