package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.ShHangMortality
import my.com.engpeng.epslaughterhouse.model.ShHangMortalityTtl

@Dao
abstract class ShHangMortalityDao : BaseDao<ShHangMortality>() {

    @Query("SELECT COUNT(*) FROM sh_hang_mortalities")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM sh_hang_mortalities WHERE sh_hang_id = :id")
    abstract suspend fun getAllByShHangId(id: Long): List<ShHangMortality>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty
        FROM sh_hang_mortalities
        WHERE sh_hang_id = :id
        """)
    abstract suspend fun getTtlByShHangId(id: Long): ShHangMortalityTtl

}