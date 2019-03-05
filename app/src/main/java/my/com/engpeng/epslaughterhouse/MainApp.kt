package my.com.engpeng.epslaughterhouse

import android.app.Application
import my.com.engpeng.epslaughterhouse.di.modules
import org.koin.android.ext.android.startKoin


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules)
    }

}