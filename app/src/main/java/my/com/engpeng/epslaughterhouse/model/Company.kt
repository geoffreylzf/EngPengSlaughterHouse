package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = Company.TABLE_NAME)
data class Company(
        @PrimaryKey var id: Long?,
        @SerializedName("company_code") @ColumnInfo(name = "company_code") var companyCode: String?,
        @SerializedName("company_name") @ColumnInfo(name = "company_name") var companyName: String?) : BaseEntity(){

    constructor(): this(null, null, null)

    companion object {
        const val TABLE_NAME = "companies"
    }

    override val tableName: String
        get() = Company.TABLE_NAME

    override val displayName: String
        get() = "Company"
}