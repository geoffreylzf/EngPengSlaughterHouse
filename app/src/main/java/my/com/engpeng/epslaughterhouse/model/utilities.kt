package my.com.engpeng.epslaughterhouse.model

import androidx.room.ColumnInfo

data class SlaughterDetailTtl(
        val ttlWeight: Double,
        val ttlQty: Int,
        val ttlCage: Int,
        val ttlCover: Int)

data class SlaughterMortalityTtl(
        val ttlWeight: Double,
        val ttlQty: Int)

data class CompanyOption(
        val id: Long,
        val isShowLocation: Boolean)


data class Bluetooth(
        val name: String,
        val address: String)

class SlaughterDisplay(
        id: Long?,
        companyId: Long?,
        locationId: Long?,
        docDate: String?,
        docNo: String,
        docType: String,
        type: String,
        truckCode: String,
        catchBtaCode: String,
        printCount: Int?,
        isUpload: Int?,
        isDelete: Int?,
        timestamp: String?,
        @ColumnInfo(name = "company_code") var companyCode: String?,
        @ColumnInfo(name = "company_name") var companyName: String?,
        @ColumnInfo(name = "location_code") var locationCode: String?,
        @ColumnInfo(name = "location_name") var locationName: String
) : Slaughter(id, companyId, locationId, docDate, docNo, docType, type, truckCode, catchBtaCode, printCount, isUpload, isDelete, timestamp)