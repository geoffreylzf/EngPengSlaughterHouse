package my.com.engpeng.epslaughterhouse.model

import java.net.HttpURLConnection

data class ApiResponse<T>(
        val cod: Int,
        val result: T) {

    fun isSuccess(): Boolean {
        val res = cod == HttpURLConnection.HTTP_OK
        if (!res) {
            throw Exception("User authentication failed")
        }
        return res
    }
}