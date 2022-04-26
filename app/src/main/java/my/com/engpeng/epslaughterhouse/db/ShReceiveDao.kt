package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.ShReceive
import my.com.engpeng.epslaughterhouse.model.ShReceiveDisplay
import my.com.engpeng.epslaughterhouse.model.SlaughterInfo


@Dao
abstract class ShReceiveDao : BaseDao<ShReceive>() {

    @Query("SELECT COUNT(*) FROM sh_receives")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM sh_receives WHERE id = :id")
    abstract suspend fun getById(id: Long): ShReceive

    @Query("""
        SELECT
        t.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM sh_receives t
        LEFT JOIN companies c ON t.company_id = c.id
        LEFT JOIN locations l ON t.location_id = l.id
        ORDER BY id DESC""")
    abstract fun getLiveAll(): LiveData<List<ShReceiveDisplay>>

    @Query("""
        SELECT
        t.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM sh_receives t
        LEFT JOIN companies c ON t.company_id = c.id
        LEFT JOIN locations l ON t.location_id = l.id
        WHERE t.id=:id """)
    abstract suspend fun getDpById(id: Long): ShReceiveDisplay

    @Query("""
        SELECT
        *
        FROM sh_receives t
        WHERE is_upload = :upload""")
    abstract suspend fun getAllByUpload(upload: Int): List<ShReceive>

    @Query("""
        SELECT
        SUM(CASE WHEN is_delete = 0 THEN 1 ELSE 0 END) AS confirm_count,
        SUM(CASE WHEN is_delete = 1 THEN 1 ELSE 0 END) AS delete_count
        FROM sh_receives
        WHERE strftime('%Y-%m-%d', timestamp) = :date""")
    abstract fun getLiveCountByDate(date: String): LiveData<SlaughterInfo>
}