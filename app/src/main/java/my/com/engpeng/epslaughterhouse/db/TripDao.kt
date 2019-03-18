package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import my.com.engpeng.epslaughterhouse.model.Trip
import my.com.engpeng.epslaughterhouse.model.TripDisplay
import my.com.engpeng.epslaughterhouse.model.TripInfo


@Dao
abstract class TripDao : BaseDao<Trip>() {

    @Query("SELECT COUNT(*) FROM trips")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM trips")
    abstract override fun deleteAll()

    @Query("SELECT * FROM trips WHERE id = :id")
    abstract fun getById(id: Long): Maybe<Trip>

    @Query("""
        SELECT
        t.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM trips t
        LEFT JOIN companies c ON t.company_id = c.id
        LEFT JOIN locations l ON t.location_id = l.id
        ORDER BY id DESC""")
    abstract fun getLiveAll(): LiveData<List<TripDisplay>>

    @Query("""
        SELECT
        t.*,
        c.company_code,
        c.company_name,
        l.location_code,
        l.location_name
        FROM trips t
        LEFT JOIN companies c ON t.company_id = c.id
        LEFT JOIN locations l ON t.location_id = l.id
        WHERE t.id=:id """)
    abstract fun getDpById(id: Long): Single<TripDisplay>

    @Query("""
        SELECT
        COUNT(*)
        FROM trips t
        WHERE is_upload = :upload""")
    abstract fun getCountByUpload(upload: Int): Single<Int>


    @Query("""
        SELECT
        COUNT(*)
        FROM trips t
        WHERE is_upload = :upload""")
    abstract fun getLiveCountByUpload(upload: Int): LiveData<Int>

    @Query("""
        SELECT
        *
        FROM trips t
        WHERE is_upload = :upload""")
    abstract fun getAllByUpload(upload: Int): Single<List<Trip>>

    @Query("""
        SELECT
        SUM(CASE WHEN is_delete = 0 THEN 1 ELSE 0 END) AS confirm_count,
        SUM(CASE WHEN is_delete = 1 THEN 1 ELSE 0 END) AS delete_count
        FROM trips
        WHERE strftime('%Y-%m-%d', timestamp) = :date""")
    abstract fun getLiveCountByDate(date: String): LiveData<TripInfo>
}