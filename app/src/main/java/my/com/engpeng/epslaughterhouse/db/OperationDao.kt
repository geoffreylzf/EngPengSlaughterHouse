package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Operation
import my.com.engpeng.epslaughterhouse.model.SlaughterInfo

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

    @Query("""
        SELECT
        SUM(CASE WHEN is_delete = 0 THEN 1 ELSE 0 END) AS confirm_count,
        SUM(CASE WHEN is_delete = 1 THEN 1 ELSE 0 END) AS delete_count
        FROM operations
        WHERE strftime('%Y-%m-%d', timestamp) = :date""")
    abstract fun getLiveCountByDate(date: String): LiveData<SlaughterInfo>
}