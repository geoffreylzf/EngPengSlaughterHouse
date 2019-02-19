package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import my.com.engpeng.epslaughterhouse.model.Company

@Dao
abstract class CompanyDao : BaseDao<Company>() {

    @Query("SELECT COUNT(*) FROM companies")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM companies")
    abstract override fun deleteAll()

    @Query("SELECT * FROM companies")
    abstract fun getAll(): Maybe<List<Company>>

    @Query("SELECT * FROM companies WHERE id = :id")
    abstract fun getById(id: Long): Observable<Company>

}