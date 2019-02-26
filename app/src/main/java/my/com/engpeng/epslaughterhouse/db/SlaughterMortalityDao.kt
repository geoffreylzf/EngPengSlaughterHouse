package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import my.com.engpeng.epslaughterhouse.model.SlaughterMortality

@Dao
abstract class SlaughterMortalityDao : BaseDao<SlaughterMortality>() {

    @Query("SELECT COUNT(*) FROM slaughter_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM slaughter_mortalities")
    abstract override fun deleteAll()

    @Query("SELECT * FROM slaughter_mortalities WHERE slaughter_id = :id")
    abstract fun getAllBySlaughterId(id: Long): Maybe<List<SlaughterMortality>>
}