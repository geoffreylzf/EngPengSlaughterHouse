package my.com.engpeng.epslaughterhouse.fragment.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location
import java.util.*

class TripHeadViewModel(private val appDb: AppDb)
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val company = appDb.companyDao().getByIdAsync(companyId)
                withContext(Dispatchers.Main) {
                    liveCompany.value = company
                }
            } catch (e: Exception) {
                liveCompany.value = null
            }
        }
    }

    fun loadLocation(locationId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val location = appDb.locationDao().getByIdAsync(locationId)
                withContext(Dispatchers.Main) {
                    liveLocation.value = location
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