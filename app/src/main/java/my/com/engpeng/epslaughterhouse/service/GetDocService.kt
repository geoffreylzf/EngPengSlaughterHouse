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
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject

class GetDocService : Service() {

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var isLocal: Boolean = false
    private var date = Sdf.getCurrentDate()

    override fun onCreate() {
        super.onCreate()

        setupNotificationBuilder()
        setupNotificationManager()
    }

    private fun setupNotificationBuilder() {
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_GET_DOC_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.get_doc))
                .setOngoing(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }
    }

    private fun setupNotificationManager() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_GET_DOC_ID,
                    getString(R.string.notification_main_channel_name),
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isLocal = intent?.getBooleanExtra(I_KEY_LOCAL, false) ?: false
        date = intent?.getStringExtra(I_KEY_DATE) ?: Sdf.getCurrentDate()
        getDoc()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getDoc() {
        initialProgress()
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            try {
                val docList = apiModule.provideApiService(isLocal).getDocListAsync(date).await().result
                appDb.docDao().deleteAll()
                delay(200)
                appDb.docDao().insert(docList)

                withContext(Dispatchers.Main) {
                    delay(200)
                    completeProgress()
                }

            }catch (e: Exception) {
                delay(200)
                errorProgress(e.message)
            }
        }
    }


    private fun initialProgress() {
        notificationBuilder
                .setContentText(getString(R.string.notification_msg_get_doc_initial))
                .setProgress(0, 0, true)
        notificationManager.notify(NOTIFICATION_GET_DOC_ID, notificationBuilder.build())
    }

    private fun completeProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText(getString(R.string.notification_msg_get_doc_complete))
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_GET_DOC_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun errorProgress(msg: String?) {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentText(getString(R.string.notification_msg_get_doc_error, msg))
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_GET_DOC_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }


    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }
}
