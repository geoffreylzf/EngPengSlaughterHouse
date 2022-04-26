package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempShHangMortality.TABLE_NAME)
data class TempShHangMortality(
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
        const val TABLE_NAME = "temp_sh_hang_mortalities"
    }

    override val tableName: String
        get() = TempShHangMortality.TABLE_NAME

    override val displayName: String
        get() = "Temp Sh Hang Mortality"
}