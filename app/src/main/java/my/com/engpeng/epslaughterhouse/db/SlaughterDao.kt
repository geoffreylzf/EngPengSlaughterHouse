package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.model.SlaughterDisplay
import my.com.engpeng.epslaughterhouse.model.TripInfo


@Dao
abstract class SlaughterDao : BaseDao<Slaughter>() {

    @Query("SELECT COUNT(*) FROM slaughters")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM slaughters")
    abstract override fun deleteAll()

    @Query("SELECT * FROM slaughters WHERE id = :id")
    abstract fun getById(id: Long): Maybe<Slaughter>

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


    @Query("""
        SELECT
        COUNT(*)
        FROM slaughters s
        WHERE is_upload = :upload""")
    abstract fun getLiveCountByUpload(upload: Int): LiveData<Int>

    @Query("""
        SELECT
        *
        FROM slaughters
        WHERE is_upload = :upload""")
    abstract fun getAllByUpload(upload: Int): Single<List<Slaughter>>

    @Query("""
        SELECT
        SUM(CASE WHEN is_delete = 0 THEN 1 ELSE 0 END) AS confirm_count,
        SUM(CASE WHEN is_delete = 1 THEN 1 ELSE 0 END) AS delete_count
        FROM slaughters
        WHERE strftime('%Y-%m-%d', timestamp) = :date""")
    abstract fun getLiveCountByDate(date: String): LiveData<TripInfo>
}