package my.com.engpeng.epslaughterhouse.model

import java.net.HttpURLConnection

data class ApiResponse<T>(
        val cod: Int,
        val result: T) {

    fun isSuccess(): Boolean{
        return cod == HttpURLConnection.HTTP_OK
    }
}