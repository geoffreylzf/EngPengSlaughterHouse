package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Location

@Dao
abstract class LocationDao : BaseDao<Location>() {

    @Query("SELECT COUNT(*) FROM locations")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("""
        SELECT *
        FROM locations
        WHERE company_id = :companyId
        ORDER BY location_name """)
    abstract suspend fun getAllByCompanyId(companyId: Long): List<Location>

    @Query("SELECT * FROM locations WHERE id = :id")
    abstract suspend fun getById(id: Long): Location

    @Query("DELETE FROM locations")
    abstract suspend fun deleteAll()
}