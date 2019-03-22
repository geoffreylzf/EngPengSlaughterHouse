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
@Entity(tableName = Operation.TABLE_NAME)
open class Operation(@PrimaryKey(autoGenerate = true) var id: Long?,
                     @SerializedName("doc_id") @ColumnInfo(name = "doc_id") var docId: Long?,
                     @SerializedName("doc_no") @ColumnInfo(name = "doc_no") var docNo: String?,
                     var remark: String?,
                     @SerializedName("is_upload") @ColumnInfo(name = "is_upload") var isUpload: Int?,
                     @SerializedName("is_delete") @ColumnInfo(name = "is_delete") var isDelete: Int?,
                     var timestamp: String?
) : BaseEntity(), Parcelable {

    @SerializedName("operation_mortality__list")
    @Ignore
    @IgnoredOnParcel
    var operationMortalityList: List<OperationMortality>? = null

    constructor() : this(
            null,
            null,
            "",
            "",
            0,
            0,
            Sdf.getCurrentDateTime())

    companion object {
        const val TABLE_NAME = "operations"
    }

    override val tableName: String
        get() = Operation.TABLE_NAME

    override val displayName: String
        get() = "Operation"
}