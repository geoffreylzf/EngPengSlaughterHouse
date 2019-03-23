package my.com.engpeng.epslaughterhouse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.model.Log
import my.com.engpeng.epslaughterhouse.model.UploadBody
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject

class UploadService : Service() {

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var isLocal: Boolean = false

    override fun onCreate() {
        super.onCreate()

        setupNotificationBuilder()
        setupNotificationManager()
    }

    private fun setupNotificationBuilder() {
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_UPLOAD_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle(getString(R.string.upload))
                .setOngoing(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }
    }

    private fun setupNotificationManager() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_UPLOAD_ID,
                    getString(R.string.notification_main_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isLocal = intent?.getBooleanExtra(I_KEY_LOCAL, false) ?: false
        upload()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun upload() {
        initialProgress()
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            try {
                val tripList = appDb.tripDao().getAllByUpload(0)
                for (trip in tripList) {
                    trip.tripDetailList = appDb.tripDetailDao().getAllByTripId(trip.id!!)
                    trip.tripMortalityList = appDb.tripMortalityDao().getAllByTripId(trip.id!!)
                }

                val operList = appDb.operationDao().getAllByUpload(0)
                for (oper in operList) {
                    oper.operationMortalityList = appDb.operationMortalityDao().getAllByOperationId(oper.id!!)
                }

                val uploadResult = apiModule.provideApiService(isLocal)
                        .uploadAsync(UploadBody(sharedPreferencesModule.getUniqueId(), tripList, operList))
                        .await().result

                var successCount = 0

                uploadResult.run {
                    for (id in tripIdList) {
                        val trip = appDb.tripDao().getById(id)
                        trip.isUpload = 1
                        appDb.tripDao().insert(trip)
                    }

                    for (id in operationIdList) {
                        val oper = appDb.operationDao().getById(id)
                        oper.isUpload = 1
                        appDb.operationDao().insert(oper)
                    }

                    successCount = tripIdList.size + operationIdList.size
                }

                appDb.logDao().insert(Log(
                        LOG_TASK_UPLOAD,
                        Sdf.getCurrentDateTime(),
                        getString(R.string.upload_log_desc, successCount)
                ))

                withContext(Dispatchers.Main) {
                    delay(200)
                    completeProgress()
                }
            } catch (e: Exception) {
                delay(200)
                errorProgress(e.message)
            }
        }
    }

    private fun initialProgress() {
        notificationBuilder
                .setContentText(getString(R.string.notification_msg_upload_initial))
                .setProgress(0, 0, true)
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
    }

    private fun completeProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentText(getString(R.string.notification_msg_upload_complete))
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun errorProgress(msg: String?) {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentText(getString(R.string.notification_msg_upload_error, msg))
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

}
