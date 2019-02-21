package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import my.com.engpeng.epslaughterhouse.model.TempSlaughterDetail
import my.com.engpeng.epslaughterhouse.model.TempSlaughterDetailTtl

@Dao
abstract class TempSlaughterDetailDao : BaseDao<TempSlaughterDetail>() {

    @Query("SELECT COUNT(*) FROM temp_slaughter_details")
    abstract override fun getLiveCount(): LiveData<Int>

    @Query("DELETE FROM temp_slaughter_details")
    abstract override fun deleteAll()

    @Query("DELETE FROM temp_slaughter_details WHERE id = :id")
    abstract fun deleteById(id: Long)

    @Query("SELECT * FROM temp_slaughter_details ORDER BY id DESC")
    abstract fun getLiveAll(): LiveData<List<TempSlaughterDetail>>

    @Query("""
        SELECT
            SUM(weight) AS ttlWeight,
            SUM(qty) AS ttlQty,
            SUM(cage) AS ttlCage,
            SUM(cover) AS ttlCover
        FROM temp_slaughter_details
        """)
    abstract fun getLiveTotal(): LiveData<TempSlaughterDetailTtl>
}