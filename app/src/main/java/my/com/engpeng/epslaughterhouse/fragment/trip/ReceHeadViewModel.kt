package my.com.engpeng.epslaughterhouse.fragment.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location
import java.util.*

class ReceHeadViewModel(private val appDb: AppDb)
    : ViewModel() {

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
        viewModelScope.launch {
            try {
                liveCompany.value = withContext(Dispatchers.IO) {
                    appDb.companyDao().getById(companyId)
                }
            } catch (e: Exception) {
                liveCompany.value = null
            }
        }
    }

    fun loadLocation(locationId: Long) {
        viewModelScope.launch {
            try {
                liveLocation.value = withContext(Dispatchers.IO) {
                    appDb.locationDao().getById(locationId)
                }
            } catch (e: Exception) {
                liveLocation.value = null
            }
        }
    }

    fun setCalendar(calendar: Calendar) {
        liveCalendar.value = calendar
    }
}