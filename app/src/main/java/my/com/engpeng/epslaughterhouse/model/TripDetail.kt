package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = TripDetail.TABLE_NAME)
data class TripDetail(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @SerializedName("trip_id") @ColumnInfo(name = "trip_id") var tripId: Long?,
        var weight: Double?,
        var cage: Int?,
        @SerializedName("house_code") @ColumnInfo(name = "house_code") var houseCode: Int?
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "trip_details"

        fun transformFromTempWithTripId(tripId: Long, tempList: List<TempTripDetail>): List<TripDetail> {
            val list: MutableList<TripDetail> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(TripDetail(null, tripId, weight, cage, houseCode))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = TripDetail.TABLE_NAME

    override val displayName: String
        get() = "Trip Detail"


}