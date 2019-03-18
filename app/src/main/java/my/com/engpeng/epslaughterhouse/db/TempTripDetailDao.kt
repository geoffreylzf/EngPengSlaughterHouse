package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import my.com.engpeng.epslaughterhouse.model.TempTripDetail
import my.com.engpeng.epslaughterhouse.model.TripDetailTtl

@Dao
abstract class TempTripDetailDao : BaseDao<TempTripDetail>() {

    @Query("SELECT COUNT(*) FROM temp_trip_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_trip_details")
    abstract override fun deleteAll()

    @Query("DELETE FROM temp_trip_details WHERE id = :id")
    abstract fun deleteById(id: Long)

    @Query("SELECT * FROM temp_trip_details ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempTripDetail>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty,
            SUM(cage) AS ttlCage,
            SUM(cover) AS ttlCover
        FROM temp_trip_details
        """)
    abstract fun getLiveTotal(): LiveData<TripDetailTtl>

    @Query("SELECT COUNT(*) FROM temp_trip_details")
    abstract fun getCount(): Maybe<Int>

    @Query("SELECT * FROM temp_trip_details")
    abstract fun getAll(): Maybe<List<TempTripDetail>>
}