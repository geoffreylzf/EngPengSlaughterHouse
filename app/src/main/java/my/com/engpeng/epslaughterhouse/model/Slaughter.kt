package my.com.engpeng.epslaughterhouse.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Slaughter(
        var id: Long?,
        var companyId: Long?,
        var locationId: Long?,
        var docDate: String?,
        var docNo: String,
        var type: String,
        var truckCode: String,
        var printCount: Int?,
        var isUpload: Int?,
        var timestamp: String?
) : Parcelable {
    constructor() : this(
            null,
            null,
            null,
            null,
            "",
            "",
            "",
            null,
            null,
            null)
}

