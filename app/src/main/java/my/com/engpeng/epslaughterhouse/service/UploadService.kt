package my.com.engpeng.epslaughterhouse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_upload.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Log
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.model.UploadBody
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject

class UploadService : Service() {

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()
    private var compositeDisposable = CompositeDisposable()

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
                .setContentTitle("Upload")
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
        preUpload()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun preUpload() {
        initialProgress()
        appDb.slaughterDao().getAllByUpload(0)
                .map { slaughterList ->
                    for (slaughter in slaughterList) {
                        appDb.slaughterDetailDao().getAllBySlaughterId(slaughter.id!!).subscribe {
                            slaughter.slaughterDetailList = it
                        }.addTo(compositeDisposable)
                        appDb.slaughterMortalityDao().getAllBySlaughterId(slaughter.id!!).subscribe {
                            slaughter.slaughterMortalityList = it
                        }.addTo(compositeDisposable)
                    }
                    slaughterList
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    upload(it)
                }, {
                    errorProgress()
                })
                .addTo(compositeDisposable)
    }

    private fun upload(slaughterList: List<Slaughter>) {
        apiModule.provideApiService(isLocal)
                .upload(UploadBody(sharedPreferencesModule.getUniqueId(), slaughterList))
                .doAfterSuccess {
                    for (id in it.result.slaughterIdList) {
                        appDb.slaughterDao().getById(id).subscribe { slaughter ->
                            slaughter.isUpload = 1
                            appDb.slaughterDao().insert(slaughter).subscribe().addTo(compositeDisposable)
                        }.addTo(compositeDisposable)
                    }

                    appDb.logDao().insert(Log(
                            LOG_TASK_UPLOAD,
                            Sdf.getCurrentDateTime(),
                            getString(R.string.upload_log_desc, it.result.slaughterIdList.size)
                            )).subscribe().addTo(compositeDisposable)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    completeProgress()
                }, {
                    errorProgress()
                })
                .addTo(compositeDisposable)
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
                .setContentText("Upload complete")
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    fun errorProgress() {
        notificationBuilder
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentText("Upload error")
                .setOngoing(false)
                .setProgress(0, 0, false)
        notificationManager.notify(NOTIFICATION_UPLOAD_ID, notificationBuilder.build())
        stopForeground(Service.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
