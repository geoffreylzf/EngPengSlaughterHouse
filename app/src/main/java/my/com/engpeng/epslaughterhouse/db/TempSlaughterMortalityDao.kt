package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempSlaughterMortality
import my.com.engpeng.epslaughterhouse.model.TempSlaughterMortalityTtl

@Dao
abstract class TempSlaughterMortalityDao : BaseDao<TempSlaughterMortality>() {

    @Query("SELECT COUNT(*) FROM temp_slaughter_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_slaughter_mortalities")
    abstract override fun deleteAll()

    @Query("DELETE FROM temp_slaughter_mortalities WHERE id = :id")
    abstract fun deleteById(id: Long)

    @Query("SELECT * FROM temp_slaughter_mortalities ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempSlaughterMortality>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty
        FROM temp_slaughter_mortalities
        """)
    abstract fun getLiveTotal(): LiveData<TempSlaughterMortalityTtl>
}