package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = TempShReceiveDetail.TABLE_NAME)
data class TempShReceiveDetail(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var weight: Double?,
        var cage: Int?,
        @SerializedName("house_no") @ColumnInfo(name = "house_no") var houseNo: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            0.0,
            0,
            0
    )

    companion object {
        const val TABLE_NAME = "temp_sh_receive_details"
    }

    override val tableName: String
        get() = TempShReceiveDetail.TABLE_NAME

    override val displayName: String
        get() = "Temp Sh Receive Detail"
}