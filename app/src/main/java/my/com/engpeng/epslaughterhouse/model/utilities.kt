package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Auth(
        val success: Boolean,
        val message: String)

abstract class BaseEntity{
    abstract val tableName: String
    abstract val displayName: String
}

data class TripDetailTtl(
        val ttlWeight: Double,
        val ttlCage: Int)

data class TripMortalityTtl(
        val ttlWeight: Double,
        val ttlQty: Int)

data class Bluetooth(
        val name: String,
        val address: String)

data class TripInfo(
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
        @SerializedName("trip") val tripList: List<Trip>
)

data class UploadResult(
        @SerializedName("trip_id_list")val tripIdList: List<Long>
)