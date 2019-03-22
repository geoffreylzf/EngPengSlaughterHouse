package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.Company

@Dao
abstract class CompanyDao : BaseDao<Company>() {

    @Query("SELECT COUNT(*) FROM companies")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("SELECT * FROM companies")
    abstract suspend fun getAll(): List<Company>

    @Query("SELECT * FROM companies WHERE id = :id")
    abstract suspend fun getById(id: Long): Company

    @Query("DELETE FROM companies")
    abstract suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM companies")
    abstract suspend fun getCount(): Int

}