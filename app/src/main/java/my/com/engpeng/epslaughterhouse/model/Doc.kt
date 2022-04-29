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
    @SerializedName("person_supplier_company_code") @ColumnInfo(name = "person_supplier_company_code") var personSupplierCompanyCode: String?,
    @SerializedName("person_supplier_company_name") @ColumnInfo(name = "person_supplier_company_name") var personSupplierCompanyName: String?
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "docs"
    }

    override val tableName: String
        get() = Doc.TABLE_NAME

    override val displayName: String
        get() = "Doc"
}