package my.com.engpeng.epslaughterhouse.di

import android.app.Application
import android.content.Context
import my.com.engpeng.epslaughterhouse.api.ApiService
import my.com.engpeng.epslaughterhouse.db.AppDb
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AppModule {
    companion object {
        fun provideApiService(): ApiService {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.8.1:8833/eperp/")
                    .build()
                    .create(ApiService::class.java)
        }

        fun provideDb(context: Context): AppDb {
            return AppDb.getInstance(context)
        }
    }
}