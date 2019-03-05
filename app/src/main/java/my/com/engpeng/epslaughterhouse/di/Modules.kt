package my.com.engpeng.epslaughterhouse.di

import androidx.room.Room
import my.com.engpeng.epslaughterhouse.db.AppDb
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module


val appModule = module {
    single { Room.databaseBuilder(androidApplication(), AppDb::class.java, "engpeng.db").build() }
    single { SharedPreferencesModule(androidApplication()) }
    single { ApiModule(get()) }
}

val modules = listOf(appModule)