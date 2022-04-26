package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.ShHang
import my.com.engpeng.epslaughterhouse.model.SlaughterInfo

@Dao
abstract class ShHangDao : BaseDao<ShHang>() {
    @Query("SELECT COUNT(*) FROM sh_hangs")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM sh_hangs ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<ShHang>>

    @Query("SELECT * FROM sh_hangs WHERE id = :id")
    abstract suspend fun getById(id : Long): ShHang

    @Query("""
        SELECT
        *
        FROM sh_hangs
        WHERE is_upload = :upload""")
    abstract suspend fun getAllByUpload(upload: Int): List<ShHang>

    @Query("""
        SELECT
        SUM(CASE WHEN is_delete = 0 THEN 1 ELSE 0 END) AS confirm_count,
        SUM(CASE WHEN is_delete = 1 THEN 1 ELSE 0 END) AS delete_count
        FROM sh_hangs
        WHERE strftime('%Y-%m-%d', timestamp) = :date""")
    abstract fun getLiveCountByDate(date: String): LiveData<SlaughterInfo>
}