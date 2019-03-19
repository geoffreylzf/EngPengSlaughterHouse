package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Doc

@Dao
abstract class DocDao : BaseDao<Doc>() {

    @Query("SELECT COUNT(*) FROM docs")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM docs")
    abstract fun getLiveAll(): LiveData<List<Doc>>

    @Query("DELETE FROM docs")
    abstract suspend fun deleteAll()
}