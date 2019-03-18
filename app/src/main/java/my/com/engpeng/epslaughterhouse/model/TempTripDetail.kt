package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempTripDetail.TABLE_NAME)
data class TempTripDetail(
        @PrimaryKey var id: Long?,
        var weight: Double?,
        var qty: Int?,
        var cage: Int?,
        var cover: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            null,
            null,
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "temp_trip_details"
    }

    override val tableName: String
        get() = TempTripDetail.TABLE_NAME

    override val displayName: String
        get() = "Temp Trip Detail"
}