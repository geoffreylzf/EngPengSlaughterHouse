package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = Location.TABLE_NAME)
data class Location(
        @PrimaryKey var id: Long?,
        @SerializedName("company_id") @ColumnInfo(name = "company_id") var companyId: Long?,
        @SerializedName("location_code") @ColumnInfo(name = "location_code") var locationCode: String?,
        @SerializedName("location_name") @ColumnInfo(name = "location_name") var locationName: String?)  : BaseEntity(){

    constructor(): this(null, null, null, null)

    companion object {
        const val TABLE_NAME = "locations"
    }

    override val tableName: String
        get() = Location.TABLE_NAME

    override val displayName: String
        get() = "Location"
}
