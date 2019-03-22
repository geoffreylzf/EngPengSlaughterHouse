package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TableLog.TABLE_NAME)
data class TableLog(
        @PrimaryKey var model: String,
        @ColumnInfo(name = "last_sync_date") var lastSyncDate: String?,
        var insert: Int?,
        var total: Int?
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "table_logs"
    }

    override val tableName: String
        get() = TableLog.TABLE_NAME

    override val displayName: String
        get() = "Table Info"
}