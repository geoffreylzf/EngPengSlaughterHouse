package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempTripMortality
import my.com.engpeng.epslaughterhouse.model.TripMortalityTtl

@Dao
abstract class TempTripMortalityDao : BaseDao<TempTripMortality>() {

    @Query("SELECT COUNT(*) FROM temp_trip_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_trip_mortalities")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_trip_mortalities WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT * FROM temp_trip_mortalities ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempTripMortality>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty
        FROM temp_trip_mortalities
        """)
    abstract fun getLiveTotal(): LiveData<TripMortalityTtl>

    @Query("SELECT * FROM temp_trip_mortalities")
    abstract suspend fun getAll(): List<TempTripMortality>
}