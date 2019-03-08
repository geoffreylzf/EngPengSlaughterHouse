package my.com.engpeng.epslaughterhouse

import android.app.Application
import my.com.engpeng.epslaughterhouse.di.modules
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin(this, modules)
    }

}