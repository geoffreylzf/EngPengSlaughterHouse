package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempTripDetail
import my.com.engpeng.epslaughterhouse.model.TripDetailTtl

@Dao
abstract class TempTripDetailDao : BaseDao<TempTripDetail>() {

    @Query("SELECT COUNT(*) FROM temp_trip_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_trip_details")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_trip_details WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT * FROM temp_trip_details ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempTripDetail>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(cage) AS ttlCage
        FROM temp_trip_details
        """)
    abstract fun getLiveTotal(): LiveData<TripDetailTtl>

    @Query("SELECT COUNT(*) FROM temp_trip_details")
    abstract suspend fun getCount(): Int

    @Query("SELECT * FROM temp_trip_details")
    abstract suspend fun getAll(): List<TempTripDetail>
}