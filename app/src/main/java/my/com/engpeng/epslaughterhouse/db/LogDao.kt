package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Log

@Dao
abstract class LogDao : BaseDao<Log>() {

    @Query("SELECT COUNT(*) FROM logs")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM logs")
    abstract override fun deleteAll()

    @Query("SELECT * FROM logs WHERE task = :task ORDER BY id DESC")
    abstract fun getLiveLogByTask(task: String): LiveData<List<Log>>

    @Query("SELECT * FROM logs WHERE task = :task ORDER BY id DESC LIMIT 1")
    abstract fun getLiveLastLogByTask(task: String): LiveData<Log>
}