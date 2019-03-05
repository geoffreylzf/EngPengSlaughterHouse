package my.com.engpeng.epslaughterhouse.di

import my.com.engpeng.epslaughterhouse.api.ApiService
import my.com.engpeng.epslaughterhouse.model.ServerUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiModule(private val sharedPreferencesModule: SharedPreferencesModule) {

    private val client: OkHttpClient

    private val apiLocalService: ApiService
    private val apiGlobalService: ApiService

    init {
        client = OkHttpClient.Builder().addInterceptor { chain ->

            var request = chain.request()
            if (request.header("No-Authentication") == null) {
                request = request.newBuilder()
                        .header("Authorization", sharedPreferencesModule.getUser().credentials)
                        .build()
            }
            chain.proceed(request)
        }.build()

        apiLocalService = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ServerUrl.getLocal())
                .client(client)
                .build()
                .create(ApiService::class.java)

        apiGlobalService = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ServerUrl.getGlobal())
                .client(client)
                .build()
                .create(ApiService::class.java)
    }

    fun provideApiService(isLocal: Boolean): ApiService {
        return when (isLocal) {
            true -> apiLocalService
            false -> apiGlobalService
        }
    }

}