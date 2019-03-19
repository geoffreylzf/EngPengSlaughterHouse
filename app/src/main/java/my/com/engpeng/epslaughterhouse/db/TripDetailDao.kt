package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TripDetail
import my.com.engpeng.epslaughterhouse.model.TripDetailTtl

@Dao
abstract class TripDetailDao : BaseDao<TripDetail>() {

    @Query("SELECT COUNT(*) FROM trip_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM trip_details WHERE trip_id = :id")
    abstract suspend fun getAllByTripId(id: Long): List<TripDetail>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty,
            SUM(cage) AS ttlCage,
            SUM(cover) AS ttlCover
        FROM trip_details
        WHERE trip_id = :id
        """)
    abstract suspend fun getTtlByTripId(id: Long): TripDetailTtl
}