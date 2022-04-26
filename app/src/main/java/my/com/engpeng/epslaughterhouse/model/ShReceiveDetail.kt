package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = ShReceiveDetail.TABLE_NAME)
data class ShReceiveDetail(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @SerializedName("sh_receive_id") @ColumnInfo(name = "sh_receive_id") var shReceiveId: Long?,
    var weight: Double?,
    var cage: Int?,
    @SerializedName("house_no") @ColumnInfo(name = "house_no") var houseNo: Int?
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "sh_receive_details"

        fun transformFromTempWithShReceiveId(shReceiveId: Long, tempList: List<TempShReceiveDetail>): List<ShReceiveDetail> {
            val list: MutableList<ShReceiveDetail> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(ShReceiveDetail(null, shReceiveId, weight, cage, houseNo))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = ShReceiveDetail.TABLE_NAME

    override val displayName: String
        get() = "Sh Receive Detail"


}