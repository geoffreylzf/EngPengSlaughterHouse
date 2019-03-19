package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = Doc.TABLE_NAME)
data class Doc(
        @PrimaryKey var id: Long?,
        @SerializedName("doc_no") @ColumnInfo(name = "doc_no") var docNo: String?,
        @SerializedName("doc_date") @ColumnInfo(name = "doc_date") var docDate: String?,
        @SerializedName("location_code") @ColumnInfo(name = "location_code") var locationCode: String?,
        @SerializedName("location_name") @ColumnInfo(name = "location_name") var locationName: String?
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "docs"
    }

    override val tableName: String
        get() = Doc.TABLE_NAME

    override val displayName: String
        get() = "Doc"
}