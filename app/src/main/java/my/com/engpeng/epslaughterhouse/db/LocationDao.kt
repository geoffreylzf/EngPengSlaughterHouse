package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import my.com.engpeng.epslaughterhouse.model.Location

@Dao
abstract class LocationDao : BaseDao<Location>() {

    @Query("SELECT COUNT(*) FROM locations")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM locations")
    abstract override fun deleteAll()

    @Query("""
        SELECT *
        FROM locations
        WHERE company_id = :companyId
        ORDER BY location_name """)
    abstract fun getAllByCompanyId(companyId: Long): Maybe<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    abstract fun getById(id: Long): Single<Location>
}