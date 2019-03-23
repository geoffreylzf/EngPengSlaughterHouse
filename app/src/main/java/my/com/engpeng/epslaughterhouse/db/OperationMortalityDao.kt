package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.OperationMortality
import my.com.engpeng.epslaughterhouse.model.OperationMortalityTtl

@Dao
abstract class OperationMortalityDao : BaseDao<OperationMortality>() {

    @Query("SELECT COUNT(*) FROM operation_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM operation_mortalities WHERE operation_id = :id")
    abstract suspend fun getAllByOperationId(id: Long): List<OperationMortality>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty
        FROM operation_mortalities
        WHERE operation_id = :id
        """)
    abstract suspend fun getTtlByOperationId(id: Long): OperationMortalityTtl

}