package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.model.SlaughterDisplay


@Dao
abstract class SlaughterDao : BaseDao<Slaughter>() {

    @Query("SELECT COUNT(*) FROM slaughters")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM slaughters")
    abstract override fun deleteAll()

    @Query("""
        SELECT
        s.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM slaughters s
        LEFT JOIN companies c ON s.company_id = c.id
        LEFT JOIN locations l ON s.location_id = l.id
        WHERE s.is_delete = 0
        ORDER BY id DESC""")
    abstract fun getLiveAll(): LiveData<List<SlaughterDisplay>>

    @Query("""
        SELECT
        s.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM slaughters s
        LEFT JOIN companies c ON s.company_id = c.id
        LEFT JOIN locations l ON s.location_id = l.id
        WHERE s.id=:id """)
    abstract fun getDpById(id: Long): Single<SlaughterDisplay>

    @Query("""
        SELECT
        COUNT(*)
        FROM slaughters s
        WHERE is_upload = :upload""")
    abstract fun getCountByUpload(upload: Int): Single<Int>
}