package my.com.engpeng.epslaughterhouse.model

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import my.com.engpeng.epslaughterhouse.util.Sdf

data class Auth(
        val success: Boolean,
        val message: String)

abstract class BaseEntity {
    abstract val tableName: String
    abstract val displayName: String
}

data class TripDetailTtl(
        val ttlWeight: Double,
        val ttlCage: Int)

data class TripMortalityTtl(
        val ttlWeight: Double,
        val ttlQty: Int)

data class OperationMortalityTtl(
        val ttlWeight: Double,
        val ttlQty: Int)

data class Bluetooth(
        val name: String,
        val address: String)

data class SlaughterInfo(
        @ColumnInfo(name = "confirm_count") var confirmCount: Int?,
        @ColumnInfo(name = "delete_count") var deleteCount: Int?
)

class TripDisplay(
        id: Long?,
        companyId: Long?,
        locationId: Long?,
        docDate: String?,
        docNo: String?,
        docType: String?,
        type: String?,
        truckCode: String?,
        catchBtaCode: String?,
        ttlQty: Int?,
        printCount: Int?,
        isUpload: Int?,
        isDelete: Int?,
        timestamp: String?,
        @ColumnInfo(name = "company_code") var companyCode: String?,
        @ColumnInfo(name = "company_name") var companyName: String?,
        @ColumnInfo(name = "location_code") var locationCode: String?,
        @ColumnInfo(name = "location_name") var locationName: String
) : Trip(id, companyId, locationId, docDate, docNo, docType, type, truckCode, catchBtaCode, ttlQty, printCount, isUpload, isDelete, timestamp)

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
        @SerializedName("unique_id") val uniqueId: String,
        @SerializedName("trip") val tripList: List<Trip>,
        @SerializedName("operation") val operationList: List<Operation>
)

data class UploadResult(
        @SerializedName("trip_id_list") val tripIdList: List<Long>,
        @SerializedName("operation_id_list") val operationIdList: List<Long>
){
    suspend fun updateStatus(context: Context, appDb: AppDb){

        val successCount: Int = tripIdList.size + operationIdList.size
        for (id in tripIdList) {
            val trip = appDb.tripDao().getById(id)
            trip.isUpload = 1
            appDb.tripDao().insert(trip)
        }

        for (id in operationIdList) {
            val oper = appDb.operationDao().getById(id)
            oper.isUpload = 1
            appDb.operationDao().insert(oper)
        }

        appDb.logDao().insert(Log(
                LOG_TASK_UPLOAD,
                Sdf.getCurrentDateTime(),
                context.getString(R.string.upload_log_desc, successCount)
        ))

    }
}