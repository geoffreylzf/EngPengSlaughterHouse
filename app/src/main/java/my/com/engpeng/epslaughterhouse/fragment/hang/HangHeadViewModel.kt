package my.com.engpeng.epslaughterhouse.fragment.hang

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HangHeadViewModel()
    : ViewModel() {

    val liveIsSelected = MutableLiveData<Boolean>()
    val liveIsQrScan = MutableLiveData<Boolean>()

    init {
        liveIsSelected.value = false
        liveIsQrScan.value = false
    }

    fun setIsSelected(b: Boolean) {
        liveIsSelected.value = b
    }

    fun setIsQrScan(b: Boolean) {
        liveIsQrScan.value = b
    }

}