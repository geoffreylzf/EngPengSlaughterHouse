package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Operation

@Dao
abstract class OperationDao : BaseDao<Operation>() {
    @Query("SELECT COUNT(*) FROM operations")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM operations ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<Operation>>

    @Query("SELECT * FROM operations WHERE id = :id")
    abstract suspend fun getById(id : Long): Operation

    @Query("""
        SELECT
        *
        FROM operations
        WHERE is_upload = :upload""")
    abstract suspend fun getAllByUpload(upload: Int): List<Operation>
}