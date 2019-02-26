package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import my.com.engpeng.epslaughterhouse.model.SlaughterDetail
import my.com.engpeng.epslaughterhouse.model.SlaughterDetailTtl

@Dao
abstract class SlaughterDetailDao : BaseDao<SlaughterDetail>() {

    @Query("SELECT COUNT(*) FROM slaughter_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM slaughter_details")
    abstract override fun deleteAll()

    @Query("SELECT * FROM slaughter_details WHERE slaughter_id = :id")
    abstract fun getAllBySlaughterId(id: Long): Maybe<List<SlaughterDetail>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty,
            SUM(cage) AS ttlCage,
            SUM(cover) AS ttlCover
        FROM slaughter_details
        WHERE slaughter_id = :id
        """)
    abstract fun getTtlBySlaughterId(id: Long): Maybe<SlaughterDetailTtl>
}