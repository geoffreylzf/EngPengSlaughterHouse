package my.com.engpeng.epslaughterhouse.api

import kotlinx.coroutines.Deferred
import my.com.engpeng.epslaughterhouse.model.*
import retrofit2.http.*

interface ApiService {

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=company")
    fun getCompanyListAsync(): Deferred<ApiResponse<List<Company>>>

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=location")
    fun getLocationListAsync(): Deferred<ApiResponse<List<Location>>>

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=processDoc")
    fun getDocListAsync(@Query("date") date: String): Deferred<ApiResponse<List<Doc>>>

    @Headers("No-Authentication: true")
    @POST("index.php?r=apiMobileAuth/NonGoogleAccLogin")
    fun loginAsync(@Header("Authorization") credentials: String): Deferred<ApiResponse<Auth>>

    //@FormUrlEncoded
    @POST("index.php?r=apiMobileSlaughterHouse/upload")
    fun uploadAsync(
            @Body uploadBody: UploadBody
    ): Deferred<ApiResponse<UploadResult>>
}