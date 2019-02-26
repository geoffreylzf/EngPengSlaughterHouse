package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = SlaughterDetail.TABLE_NAME)
data class SlaughterDetail(
        @PrimaryKey var id: Long?,
        @SerializedName("slaughter_id") @ColumnInfo(name = "slaughter_id") var slaughterId: Long?,
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
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "slaughter_details"

        fun transformFromTempWithSlaughterId(slaughterId: Long, tempList: List<TempSlaughterDetail>): List<SlaughterDetail> {
            val list: MutableList<SlaughterDetail> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(SlaughterDetail(null, slaughterId, weight, qty, cage, cover))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = SlaughterDetail.TABLE_NAME

    override val displayName: String
        get() = "Slaughter Detail"


}