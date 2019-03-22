package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Operation

@Dao
abstract class OperationDao : BaseDao<Operation>() {
    @Query("SELECT COUNT(*) FROM operations")
    abstract override fun getLiveCount(): LiveData<Int>
}