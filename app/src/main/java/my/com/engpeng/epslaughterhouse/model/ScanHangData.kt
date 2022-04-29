package my.com.engpeng.epslaughterhouse.model

class ScanHangData() {
    var version: String? = null
    var uuid: String? = null
    var docNo: String? = null

    constructor(scan: String) : this() {
        val arr = scan.split("|")
        version = arr[0]
        if (version.equals("v1")) {
            uuid = arr[1]
            docNo = arr[2]
        } else {
            throw Exception("Invalid Qr Code")
        }
    }

}