package my.com.engpeng.epslaughterhouse.api

import io.reactivex.Observable
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.ApiResponse
import my.com.engpeng.epslaughterhouse.model.Location
import retrofit2.http.GET

interface ApiService {
    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=company")
    fun getCompanyList(): Observable<ApiResponse<List<Company>>>

    @GET("index.php?r=apiMobileSlaughterHouse/getHouseKeeping&type=location")
    fun getLocationList(): Observable<ApiResponse<List<Location>>>
}