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
@Entity(tableName = ShHang.TABLE_NAME)
open class ShHang(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @SerializedName("sh_receive_uuid") @ColumnInfo(name = "sh_receive_uuid") var shReceiveUuid: String?,
    @SerializedName("doc_id") @ColumnInfo(name = "doc_id") var docId: Long?,
    @SerializedName("doc_no") @ColumnInfo(name = "doc_no") var docNo: String?,
    var remark: String?,
    @SerializedName("is_upload") @ColumnInfo(name = "is_upload") var isUpload: Int?,
    @SerializedName("is_delete") @ColumnInfo(name = "is_delete") var isDelete: Int?,
    var timestamp: String?
) : BaseEntity(), Parcelable {

    @SerializedName("mortalities")
    @Ignore
    @IgnoredOnParcel
    var shHangMortalityList: List<ShHangMortality>? = null

    constructor() : this(
        null,
        null,
        null,
        "",
        "",
        0,
        0,
        Sdf.getCurrentDateTime()
    )

    companion object {
        const val TABLE_NAME = "sh_hangs"
    }

    override val tableName: String
        get() = ShHang.TABLE_NAME

    override val displayName: String
        get() = "ShHang"
}