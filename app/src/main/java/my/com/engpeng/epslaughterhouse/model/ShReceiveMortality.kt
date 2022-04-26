package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = ShReceiveMortality.TABLE_NAME)
data class ShReceiveMortality(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @SerializedName("sh_receive_id") @ColumnInfo(name = "sh_receive_id") var shReceiveId: Long?,
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
        const val TABLE_NAME = "sh_receive_mortalities"

        fun transformFromTempWithShReceiveId(shReceiveId: Long, tempList: List<TempShReceiveMortality>): List<ShReceiveMortality> {
            val list: MutableList<ShReceiveMortality> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(ShReceiveMortality(null, shReceiveId, weight, qty))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = ShReceiveMortality.TABLE_NAME

    override val displayName: String
        get() = "Sh Receive Mortality"
}