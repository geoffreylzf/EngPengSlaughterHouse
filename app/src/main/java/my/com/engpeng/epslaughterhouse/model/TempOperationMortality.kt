package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempOperationMortality.TABLE_NAME)
data class TempOperationMortality(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var weight: Double?,
        var qty: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "temp_operation_mortalities"
    }

    override val tableName: String
        get() = TempOperationMortality.TABLE_NAME

    override val displayName: String
        get() = "Temp Operation Mortality"
}