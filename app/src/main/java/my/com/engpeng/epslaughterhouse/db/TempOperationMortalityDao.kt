package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempOperationMortality

@Dao
abstract class TempOperationMortalityDao : BaseDao<TempOperationMortality>() {

    @Query("SELECT COUNT(*) FROM temp_operation_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM temp_operation_mortalities ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempOperationMortality>>

    @Query("DELETE FROM temp_operation_mortalities")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_operation_mortalities WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM temp_operation_mortalities")
    abstract suspend fun getCount(): Int

    @Query("SELECT * FROM temp_operation_mortalities")
    abstract suspend fun getAll(): List<TempOperationMortality>
}