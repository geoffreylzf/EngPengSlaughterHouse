package my.com.engpeng.epslaughterhouse.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import my.com.engpeng.epslaughterhouse.util.Sdf

data class Auth(
    val success: Boolean,
    val message: String
)

abstract class BaseEntity {
    abstract val tableName: String
    abstract val displayName: String
}

data class ShReceiveDetailTtl(
    val ttlWeight: Double,
    val ttlCage: Int
)

data class ShReceiveMortalityTtl(
    val ttlWeight: Double,
    val ttlQty: Int
)

data class ShHangMortalityTtl(
    val ttlWeight: Double,
    val ttlQty: Int
)

data class Bluetooth(
    val name: String,
    val address: String
)

data class SlaughterInfo(
    @ColumnInfo(name = "confirm_count") var confirmCount: Int?,
    @ColumnInfo(name = "delete_count") var deleteCount: Int?
)

@SuppressLint("ParcelCreator")
class ShReceiveDisplay(
    id: Long?,
    uuid: String?,
    companyId: Long?,
    locationId: Long?,
    docDate: String?,
    docNo: String?,
    docType: String?,
    type: String?,
    truckCode: String?,
    catchBtaCode: String?,
    ttlQty: Int?,
    ttlCageQty: Int?,
    ttlCoverQty: Int?,
    isUpload: Int?,
    isDelete: Int?,
    timestamp: String?,
    @ColumnInfo(name = "company_code") var companyCode: String?,
    @ColumnInfo(name = "company_name") var companyName: String?,
    @ColumnInfo(name = "location_code") var locationCode: String?,
    @ColumnInfo(name = "location_name") var locationName: String
) : ShReceive(
    id,
    uuid,
    companyId, locationId, docDate, docNo, docType, type, truckCode, catchBtaCode,
    ttlQty, ttlCageQty, ttlCoverQty,
    isUpload, isDelete, timestamp
)

class ServerUrl {
    companion object {

        private const val DEFAULT_GLOBAL_URL = "http://epgroup.dlinkddns.com:5030/eperp/"
        private const val DEFAULT_LOCAL_URL = "http://192.168.8.1:8833/eperp/"

        fun getGlobal(): String {
            return DEFAULT_GLOBAL_URL
        }

        fun getLocal(): String {
            return DEFAULT_LOCAL_URL
        }
    }
}

data class UploadBody(
    @SerializedName("sh_receive") val shReceiveList: List<ShReceive>,
    @SerializedName("sh_hang") val shHangList: List<ShHang>
)

data class UploadResult(
    @SerializedName("sh_receive_id_list") val receIdList: List<Long>,
    @SerializedName("sh_hang_id_list") val hangIdList: List<Long>
) {
    suspend fun updateStatus(context: Context, appDb: AppDb) {

        val successCount: Int = receIdList.size + hangIdList.size
        for (id in receIdList) {
            val rece = appDb.shReceiveDao().getById(id)
            rece.isUpload = 1
            appDb.shReceiveDao().insert(rece)
        }

        for (id in hangIdList) {
            val hang = appDb.shHangDao().getById(id)
            hang.isUpload = 1
            appDb.shHangDao().insert(hang)
        }

        appDb.logDao().insert(
            Log(
                LOG_TASK_UPLOAD,
                Sdf.getCurrentDateTime(),
                context.getString(R.string.upload_log_desc, successCount)
            )
        )

    }
}