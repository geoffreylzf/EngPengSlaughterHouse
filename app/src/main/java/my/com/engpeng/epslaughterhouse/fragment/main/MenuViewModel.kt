package my.com.engpeng.epslaughterhouse.fragment.main

import androidx.lifecycle.ViewModel
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import my.com.engpeng.epslaughterhouse.util.Sdf

class MenuViewModel(appDb: AppDb)
    : ViewModel() {

    val liveUnuploadCount = appDb.tripDao().getLiveCountByUpload(0)
    val liveLastUploadLog = appDb.logDao().getLiveLastLogByTask(LOG_TASK_UPLOAD)
    val liveTripCount = appDb.tripDao().getLiveCountByDate(Sdf.getCurrentDate())
    val liveDocList = appDb.docDao().getLiveAll()

}