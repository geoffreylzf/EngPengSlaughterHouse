package my.com.engpeng.epslaughterhouse.di

import android.content.Context
import android.content.SharedPreferences
import my.com.engpeng.epslaughterhouse.BuildConfig
import my.com.engpeng.epslaughterhouse.api.ApiService
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.ServerUrl
import my.com.engpeng.epslaughterhouse.util.SharedPreferencesUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


const val PREFERENCE_FILE_KEY = BuildConfig.APPLICATION_ID

class AppModule {
    companion object {

        private fun provideClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor { chain ->

                var request = chain.request()
                if (request.header("No-Authentication") == null) {
                    request = request.newBuilder()
                            .header("Authorization", SharedPreferencesUtils.getUser(context).credentials)
                            .build()
                }
                chain.proceed(request)
            }.build()
        }

        fun provideApiModule(context: Context): ApiModule {
            return ApiModule(context)
        }

        fun provideDb(context: Context): AppDb {
            return AppDb.getInstance(context)
        }

        fun providePreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        }
    }

    class ApiModule(val context: Context) {
        fun provideApiService(isLocal: Boolean): ApiService {
            if (isLocal) {
                return Retrofit.Builder()
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(ServerUrl.getLocal())
                        .client(provideClient(context))
                        .build()
                        .create(ApiService::class.java)
            } else {
                return Retrofit.Builder()
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(ServerUrl.getGlobal())
                        .client(provideClient(context))
                        .build()
                        .create(ApiService::class.java)
            }
        }
    }
}