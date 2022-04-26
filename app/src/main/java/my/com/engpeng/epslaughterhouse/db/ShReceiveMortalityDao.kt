package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.ShReceiveMortality

@Dao
abstract class ShReceiveMortalityDao : BaseDao<ShReceiveMortality>() {

    @Query("SELECT COUNT(*) FROM sh_receive_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM sh_receive_mortalities WHERE sh_receive_id = :id")
    abstract suspend fun getAllByShReceiveId(id: Long): List<ShReceiveMortality>
}