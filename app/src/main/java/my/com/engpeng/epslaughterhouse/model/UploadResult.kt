package my.com.engpeng.epslaughterhouse.model

import com.google.gson.annotations.SerializedName

data class UploadResult(
        @SerializedName("trip_id_list")val tripIdList: List<Long>
)