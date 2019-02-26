package my.com.engpeng.epslaughterhouse.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = Slaughter.TABLE_NAME)
open class Slaughter(@PrimaryKey var id: Long?,
                     @SerializedName("company_id") @ColumnInfo(name = "company_id") var companyId: Long?,
                     @SerializedName("location_id") @ColumnInfo(name = "location_id") var locationId: Long?,
                     @SerializedName("doc_date") @ColumnInfo(name = "doc_date") var docDate: String?,
                     @SerializedName("doc_no") @ColumnInfo(name = "doc_no") var docNo: String,
                     @SerializedName("doc_type") @ColumnInfo(name = "doc_tpe") var docType: String,
                     var type: String,
                     @SerializedName("truck_code") @ColumnInfo(name = "truck_code") var truckCode: String,
                     @SerializedName("print_count") @ColumnInfo(name = "print_count") var printCount: Int?,
                     @SerializedName("is_upload") @ColumnInfo(name = "is_upload") var isUpload: Int?,
                     @SerializedName("is_delete") @ColumnInfo(name = "is_delete") var isDelete: Int?,
                     var timestamp: String?
) : BaseEntity(), Parcelable {

    constructor() : this(
            null,
            null,
            null,
            null,
            "",
            "",
            "",
            "",
            0,
            0,
            0,
            null)

    companion object {
        const val TABLE_NAME = "slaughters"
    }

    override val tableName: String
        get() = Slaughter.TABLE_NAME

    override val displayName: String
        get() = "Slaughter"
}

