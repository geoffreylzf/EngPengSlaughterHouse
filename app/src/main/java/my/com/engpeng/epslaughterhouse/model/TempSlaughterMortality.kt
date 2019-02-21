package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempSlaughterMortality.TABLE_NAME)
data class TempSlaughterMortality(
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
        const val TABLE_NAME = "temp_slaughter_mortalities"
    }

    override val tableName: String
        get() = TempSlaughterMortality.TABLE_NAME

    override val displayName: String
        get() = "Temp Slaughter Mortality"
}