package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempTripMortality.TABLE_NAME)
data class TempTripMortality(
        @PrimaryKey var id: Long?,
        var weight: Double?,
        var qty: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "temp_trip_mortalities"
    }

    override val tableName: String
        get() = TempTripMortality.TABLE_NAME

    override val displayName: String
        get() = "Temp Trip Mortality"
}