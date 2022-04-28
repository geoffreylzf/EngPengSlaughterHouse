package my.com.engpeng.epslaughterhouse.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrintData(
    val qrText: String? = null,
    val printText: String? = null
) : Parcelable