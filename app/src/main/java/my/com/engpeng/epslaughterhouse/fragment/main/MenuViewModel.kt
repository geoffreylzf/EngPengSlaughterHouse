package my.com.engpeng.epslaughterhouse.fragment.main

import androidx.lifecycle.ViewModel
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import my.com.engpeng.epslaughterhouse.util.Sdf

class MenuViewModel(appDb: AppDb)
    : ViewModel() {

    val liveUnuploadCount = appDb.utilDao().getLiveUnuploadCount()
    val liveLastUploadLog = appDb.logDao().getLiveLastLogByTask(LOG_TASK_UPLOAD)
    val liveReceCount = appDb.shReceiveDao().getLiveCountByDate(Sdf.getCurrentDate())
    val liveHangCount = appDb.shHangDao().getLiveCountByDate(Sdf.getCurrentDate())
    val liveDocList = appDb.docDao().getLiveAll()

}