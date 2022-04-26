package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.ShReceiveDetail
import my.com.engpeng.epslaughterhouse.model.ShReceiveDetailTtl

@Dao
abstract class ShReceiveDetailDao : BaseDao<ShReceiveDetail>() {

    @Query("SELECT COUNT(*) FROM sh_receive_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM sh_receive_details WHERE sh_receive_id = :id")
    abstract suspend fun getAllByShReceiveId(id: Long): List<ShReceiveDetail>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(cage) AS ttlCage
        FROM sh_receive_details
        WHERE sh_receive_id = :id
        """)
    abstract suspend fun getTtlByShReceiveId(id: Long): ShReceiveDetailTtl
}