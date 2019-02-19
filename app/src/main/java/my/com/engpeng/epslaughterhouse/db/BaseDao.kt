package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import my.com.engpeng.epslaughterhouse.model.BaseEntity

@Dao
abstract class BaseDao<T: BaseEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg ts: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(ts: List<T>)

    abstract fun getLiveCount(): LiveData<Int>

    abstract fun deleteAll()
}