package my.com.engpeng.epslaughterhouse.model

import com.google.gson.annotations.SerializedName

data class UploadBody(
        @SerializedName("unique_id") val uniqueId: String,
        @SerializedName("trip") val tripList: List<Trip>
)