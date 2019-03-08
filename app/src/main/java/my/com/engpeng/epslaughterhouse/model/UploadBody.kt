package my.com.engpeng.epslaughterhouse.model

import com.google.gson.annotations.SerializedName

data class UploadBody(
        @SerializedName("slaughter") val slaughterList: List<Slaughter>
)