package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempShReceiveDetail
import my.com.engpeng.epslaughterhouse.model.ShReceiveDetailTtl

@Dao
abstract class TempShReceiveDetailDao : BaseDao<TempShReceiveDetail>() {

    @Query("SELECT COUNT(*) FROM temp_sh_receive_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_sh_receive_details")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM temp_sh_receive_details WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("SELECT * FROM temp_sh_receive_details ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempShReceiveDetail>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(cage) AS ttlCage
        FROM temp_sh_receive_details
        """)
    abstract fun getLiveTotal(): LiveData<ShReceiveDetailTtl>

    @Query("SELECT COUNT(*) FROM temp_sh_receive_details")
    abstract suspend fun getCount(): Int

    @Query("SELECT * FROM temp_sh_receive_details")
    abstract suspend fun getAll(): List<TempShReceiveDetail>
}