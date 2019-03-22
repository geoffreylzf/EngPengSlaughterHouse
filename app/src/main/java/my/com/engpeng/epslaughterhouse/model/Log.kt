package my.com.engpeng.epslaughterhouse.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Log.TABLE_NAME)
class Log(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var task: String?,
        var datetime: String?,
        var remark: String?
) : BaseEntity() {

    constructor(task: String, datetime: String, remark: String) : this(null, task, datetime, remark)

    companion object {
        const val TABLE_NAME = "logs"
    }

    override val tableName: String
        get() = Log.TABLE_NAME

    override val displayName: String
        get() = "Log"
}