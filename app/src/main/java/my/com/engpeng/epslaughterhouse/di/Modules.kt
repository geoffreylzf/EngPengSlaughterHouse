package my.com.engpeng.epslaughterhouse.di

import androidx.room.Room
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.db.DATABASE_NAME
import my.com.engpeng.epslaughterhouse.fragment.main.MenuViewModel
import my.com.engpeng.epslaughterhouse.fragment.hang.HangHeadViewModel
import my.com.engpeng.epslaughterhouse.fragment.receive.ReceHeadViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val appModule = module {
    single { Room.databaseBuilder(androidApplication(), AppDb::class.java, DATABASE_NAME).build() }
    single { SharedPreferencesModule(androidApplication()) }
    single { ApiModule(get()) }
    single { PrintModule(androidApplication(), get()) }
    single { WorkManagerModule() }
}

val viewModelModule = module {
    viewModel { ReceHeadViewModel(get()) }
    viewModel { MenuViewModel(get()) }
    viewModel { HangHeadViewModel() }
}

val modules = listOf(appModule, viewModelModule)