package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = SlaughterMortality.TABLE_NAME)
data class SlaughterMortality(
        @PrimaryKey var id: Long?,
        @SerializedName("slaughter_id") @ColumnInfo(name = "slaughter_id") var slaughterId: Long?,
        var weight: Double?,
        var qty: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            null,
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "slaughter_mortalities"

        fun transformFromTempWithSlaughterId(slaughterId: Long, tempList: List<TempSlaughterMortality>): List<SlaughterMortality> {
            val list: MutableList<SlaughterMortality> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(SlaughterMortality(null, slaughterId, weight, qty))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = SlaughterMortality.TABLE_NAME

    override val displayName: String
        get() = "Slaughter Mortality"
}