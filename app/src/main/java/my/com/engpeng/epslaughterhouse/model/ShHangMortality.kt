package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = ShHangMortality.TABLE_NAME)
data class ShHangMortality(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @SerializedName("sh_hang_id") @ColumnInfo(name = "sh_hang_id") var shHangId: Long?,
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
        const val TABLE_NAME = "sh_hang_mortalities"

        fun transformFromTempWithShHangId(shHangId: Long, tempList: List<TempShHangMortality>): List<ShHangMortality> {
            val list: MutableList<ShHangMortality> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(ShHangMortality(null, shHangId, weight, qty))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = ShHangMortality.TABLE_NAME

    override val displayName: String
        get() = "Sh Hang Mortality"
}