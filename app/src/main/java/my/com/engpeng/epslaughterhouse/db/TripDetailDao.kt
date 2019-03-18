package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import my.com.engpeng.epslaughterhouse.model.TripDetail
import my.com.engpeng.epslaughterhouse.model.TripDetailTtl

@Dao
abstract class TripDetailDao : BaseDao<TripDetail>() {

    @Query("SELECT COUNT(*) FROM trip_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM trip_details")
    abstract override fun deleteAll()

    @Query("SELECT * FROM trip_details WHERE trip_id = :id")
    abstract fun getAllByTripId(id: Long): Maybe<List<TripDetail>>

    @Query("SELECT * FROM trip_details WHERE trip_id = :id")
    abstract suspend fun getAllByTripIdAsync(id: Long): List<TripDetail>

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