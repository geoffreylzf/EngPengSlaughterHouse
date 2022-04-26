package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempShReceiveMortality
import my.com.engpeng.epslaughterhouse.model.ShReceiveMortalityTtl

@Dao
abstract class TempShReceiveMortalityDao : BaseDao<TempShReceiveMortality>() {

    @Query("SELECT COUNT(*) FROM temp_sh_receive_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_sh_receive_mortalities")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_sh_receive_mortalities WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT * FROM temp_sh_receive_mortalities ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempShReceiveMortality>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty
        FROM temp_sh_receive_mortalities
        """)
    abstract fun getLiveTotal(): LiveData<ShReceiveMortalityTtl>

    @Query("SELECT * FROM temp_sh_receive_mortalities")
    abstract suspend fun getAll(): List<TempShReceiveMortality>
}