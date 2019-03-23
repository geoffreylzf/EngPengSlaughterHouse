package my.com.engpeng.epslaughterhouse.fragment.operation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OperHeadViewModel()
    : ViewModel() {

    val liveIsSelected = MutableLiveData<Boolean>()

    init {
        liveIsSelected.value = false
    }

    fun setIsSelected(b: Boolean) {
        liveIsSelected.value = b
    }

}