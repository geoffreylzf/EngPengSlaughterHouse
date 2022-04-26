package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempShHangMortality

@Dao
abstract class TempShHangMortalityDao : BaseDao<TempShHangMortality>() {

    @Query("SELECT COUNT(*) FROM temp_sh_hang_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM temp_sh_hang_mortalities ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempShHangMortality>>

    @Query("DELETE FROM temp_sh_hang_mortalities")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_sh_hang_mortalities WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM temp_sh_hang_mortalities")
    abstract suspend fun getCount(): Int

    @Query("SELECT * FROM temp_sh_hang_mortalities")
    abstract suspend fun getAll(): List<TempShHangMortality>
}