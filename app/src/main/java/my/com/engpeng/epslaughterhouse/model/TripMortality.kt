package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = TripMortality.TABLE_NAME)
data class TripMortality(
        @PrimaryKey var id: Long?,
        @SerializedName("trip_id") @ColumnInfo(name = "trip_id") var tripId: Long?,
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
        const val TABLE_NAME = "trip_mortalities"

        fun transformFromTempWithTripId(tripId: Long, tempList: List<TempTripMortality>): List<TripMortality> {
            val list: MutableList<TripMortality> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(TripMortality(null, tripId, weight, qty))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = TripMortality.TABLE_NAME

    override val displayName: String
        get() = "Trip Mortality"
}