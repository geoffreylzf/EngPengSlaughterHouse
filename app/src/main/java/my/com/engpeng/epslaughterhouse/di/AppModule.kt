package my.com.engpeng.epslaughterhouse.di

import android.content.Context
import android.content.SharedPreferences
import my.com.engpeng.epslaughterhouse.BuildConfig
import my.com.engpeng.epslaughterhouse.api.ApiService
import my.com.engpeng.epslaughterhouse.db.AppDb
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val PREFERENCE_FILE_KEY = BuildConfig.APPLICATION_ID

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

        fun providePreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        }
    }
}