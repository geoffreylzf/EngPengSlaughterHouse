package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TempSlaughterDetail.TABLE_NAME)
data class TempSlaughterDetail(
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
        const val TABLE_NAME = "temp_slaughter_details"
    }

    override val tableName: String
        get() = TempSlaughterDetail.TABLE_NAME

    override val displayName: String
        get() = "Temp Slaughter Detail"
}