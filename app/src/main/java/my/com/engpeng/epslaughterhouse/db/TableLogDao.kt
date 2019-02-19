package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TableLog

@Dao
abstract class TableLogDao: BaseDao<TableLog>(){

    @Query("SELECT COUNT(*) FROM table_logs")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM table_logs WHERE model = :model")
    abstract fun getLiveByModel(model : String): LiveData<TableLog>

    @Query("DELETE FROM table_logs")
    abstract override fun deleteAll()
}