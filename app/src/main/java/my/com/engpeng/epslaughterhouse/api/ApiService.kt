package my.com.engpeng.epslaughterhouse.api

import io.reactivex.Observable
import io.reactivex.Single
import my.com.engpeng.epslaughterhouse.model.*
import retrofit2.http.*

interface ApiService {

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=company")
    fun getCompanyList(): Observable<ApiResponse<List<Company>>>

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=location")
    fun getLocationList(): Observable<ApiResponse<List<Location>>>


    @FormUrlEncoded
    @Headers("No-Authentication: true")
    @POST("index.php?r=apiMobileAuth/login")
    fun login(
            @Header("Authorization") credentials: String,
            @Field("email") email: String?
    ): Observable<ApiResponse<Auth>>

    //@FormUrlEncoded
    @POST("index.php?r=apiMobileSlaughterHouse/upload")
    fun upload(
            @Body uploadBody: UploadBody
    ): Single<ApiResponse<UploadResult>>
}