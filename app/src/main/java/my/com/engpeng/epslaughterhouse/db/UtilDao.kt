package my.com.engpeng.epslaughterhouse.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class UtilDao {
    @Query("""
        SELECT SUM(count)
        FROM
        (SELECT COUNT(*) as count FROM trips WHERE is_upload = 0
        UNION ALL
        SELECT COUNT(*) as count FROM operations WHERE is_upload = 0) A
        """)
    abstract fun getLiveUnuploadCount(): LiveData<Int>

    @Query("""
        SELECT SUM(count)
        FROM
        (SELECT COUNT(*) as count FROM trips WHERE is_upload = 0
        UNION
        SELECT COUNT(*) as count FROM operations WHERE is_upload = 0) A""")
    abstract suspend fun getUnuploadCount(): Int
}