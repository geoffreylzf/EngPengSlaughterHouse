package my.com.engpeng.epslaughterhouse.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import my.com.engpeng.epslaughterhouse.util.Sdf


@Parcelize
@Entity(tableName = ShReceive.TABLE_NAME)
open class ShReceive(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @SerializedName("company_id") @ColumnInfo(name = "company_id") var companyId: Long?,
                     @SerializedName("location_id") @ColumnInfo(name = "location_id") var locationId: Long?,
                     @SerializedName("doc_date") @ColumnInfo(name = "doc_date") var docDate: String?,
                     @SerializedName("doc_no") @ColumnInfo(name = "doc_no") var docNo: String?,
                     @SerializedName("doc_type") @ColumnInfo(name = "doc_type") var docType: String?,
                     var type: String?,
                     @SerializedName("truck_code") @ColumnInfo(name = "truck_code") var truckCode: String?,
                     @SerializedName("catch_bta_code") @ColumnInfo(name = "catch_bta_code") var catchBtaCode: String?,
                     @SerializedName("ttl_qty") @ColumnInfo(name = "ttl_qty") var ttlQty: Int?,
                     @SerializedName("print_count") @ColumnInfo(name = "print_count") var printCount: Int?,
                     @SerializedName("is_upload") @ColumnInfo(name = "is_upload") var isUpload: Int?,
                     @SerializedName("is_delete") @ColumnInfo(name = "is_delete") var isDelete: Int?,
                     var timestamp: String?
) : BaseEntity(), Parcelable {

    @SerializedName("sh_receive_detail_list")
    @Ignore
    @IgnoredOnParcel
    var shReceiveDetailList: List<ShReceiveDetail>? = null

    @SerializedName("sh_receive_mortality_list")
    @Ignore
    @IgnoredOnParcel
    var shReceiveMortalityList: List<ShReceiveMortality>? = null

    @IgnoredOnParcel
    @Ignore
    var houseStr: String = ""

    constructor() : this(
            null,
            null,
            null,
            null,
            "",
            "",
            "",
            "",
            "",
            0,
            0,
            0,
            0,
            Sdf.getCurrentDateTime())

    companion object {
        const val TABLE_NAME = "sh_receives"
    }

    override val tableName: String
        get() = ShReceive.TABLE_NAME

    override val displayName: String
        get() = "Sh Receive"
}

