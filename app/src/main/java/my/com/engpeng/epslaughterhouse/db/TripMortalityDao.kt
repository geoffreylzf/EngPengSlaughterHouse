package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TripMortality

@Dao
abstract class TripMortalityDao : BaseDao<TripMortality>() {

    @Query("SELECT COUNT(*) FROM trip_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM trip_mortalities WHERE trip_id = :id")
    abstract suspend fun getAllByTripId(id: Long): List<TripMortality>
}