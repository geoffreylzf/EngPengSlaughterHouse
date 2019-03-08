package my.com.engpeng.epslaughterhouse.model

import com.google.gson.annotations.SerializedName

data class UploadResult(
        @SerializedName("slaughter_id_list")val slaughterIdList: List<Long>
)