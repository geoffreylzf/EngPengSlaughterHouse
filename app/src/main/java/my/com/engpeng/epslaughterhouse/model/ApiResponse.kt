package my.com.engpeng.epslaughterhouse.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
        val cod: Int,
        val result: T)