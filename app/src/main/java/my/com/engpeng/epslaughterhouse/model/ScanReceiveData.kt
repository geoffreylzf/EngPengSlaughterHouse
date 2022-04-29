package my.com.engpeng.epslaughterhouse.model


private const val VERSION_1 = "v1"

class ScanReceiveData() {

    var version: String? = null
    var companyId: Long? = null
    var locationId: Long? = null
    var docDate: String? = null
    var docNo: String? = null
    var docType: String? = null
    var type: String? = null
    var truckCode: String? = null
    var catchBtaCode: String? = null
    var houseStr: String? = null
    var ttlQty: Int? = null
    var ttlCageQty: Int? = null
    var ttlCoverQty: Int? = null

    constructor(scan: String) : this() {
        val arr = scan.split("|")
        version = arr[0]
        if (version.equals(VERSION_1)) {
            companyId = arr[1].toLong()
            locationId = arr[2].toLong()
            docDate = arr[3]
            docNo = arr[4]
            docType = arr[5]
            type = arr[6]
            truckCode = arr[7]
            catchBtaCode = arr[8]
            houseStr = arr[9]
            ttlQty = arr[10].toInt()
            ttlCageQty = arr[11].toIntOrNull() ?: 0
            ttlCoverQty = arr[12].toIntOrNull() ?: 0
        } else {
            throw Exception("Invalid Qr Code")
        }
    }
}