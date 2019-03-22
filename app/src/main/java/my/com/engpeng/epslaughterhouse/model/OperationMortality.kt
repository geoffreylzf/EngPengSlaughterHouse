package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = OperationMortality.TABLE_NAME)
data class OperationMortality(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @SerializedName("operation_id") @ColumnInfo(name = "operation_id") var operationId: Long?,
        var weight: Double?,
        var qty: Int?
) : BaseEntity() {
    constructor() : this(
            null,
            null,
            null,
            null
    )

    companion object {
        const val TABLE_NAME = "operation_mortalities"

        fun transformFromTempWithOperationId(operationId: Long, tempList: List<TempOperationMortality>): List<OperationMortality> {
            val list: MutableList<OperationMortality> = mutableListOf()
            for (temp in tempList) {
                temp.run {
                    list.add(OperationMortality(null, operationId, weight, qty))
                }
            }
            return list
        }
    }

    override val tableName: String
        get() = OperationMortality.TABLE_NAME

    override val displayName: String
        get() = "Operation Mortality"
}