package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = TempTripDetail.TABLE_NAME)
data class TempTripDetail(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var weight: Double?,
        var cage: Int?,
        @SerializedName("house_code") @ColumnInfo(name = "house_code") var houseCode: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            0.0,
            0,
            0
    )

    companion object {
        const val TABLE_NAME = "temp_trip_details"
    }

    override val tableName: String
        get() = TempTripDetail.TABLE_NAME

    override val displayName: String
        get() = "Temp Trip Detail"
}