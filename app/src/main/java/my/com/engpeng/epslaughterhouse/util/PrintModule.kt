package my.com.engpeng.epslaughterhouse.util

import android.content.Context
import my.com.engpeng.epslaughterhouse.db.AppDb
import java.util.*

class PrintModule(val context: Context, private val appDb: AppDb) {

    companion object {
        private const val PRINT_END = "\n\n\n\n\n"
        private const val PRINT_SEPERATOR = "---------------------------------------------"
        private const val PRINT_HALF_SEPERATOR = "----------------------"
        private const val LINE_CHAR_COUNT = 45

        private const val TRIP_LIVE_BIRD_HEADER = "  #   Weight  Cage #H "
        private const val TRIP_DEAD_BIRD_HEADER = "  #   Weight   Qty    "

        private const val OPERATION_HEADER = "  #   Weight   Qty    "
    }

    private fun formatLine(line: String): String {
        if (line.length > LINE_CHAR_COUNT) {

            val stringBuilder = StringBuilder()
            val count = Math.ceil(line.length.toDouble() / LINE_CHAR_COUNT.toDouble()).toInt()

            for (i in 0 until count) {
                val start = i * LINE_CHAR_COUNT
                var end = (i + 1) * LINE_CHAR_COUNT
                if (end > line.length) {
                    end = line.length
                }
                stringBuilder.append(line.substring(start, end))
                stringBuilder.append("\n")
            }
            return stringBuilder.toString()

        } else {
            return String.format("%-" + LINE_CHAR_COUNT + "s\n", line)
        }
    }

    private fun halfLine(halfLine: String): String {
        return String.format("%-22s", if (halfLine.length > 22) halfLine.substring(0, 22) else halfLine)
    }

    private fun formatNumber(length: Int, house_code: Int): String {
        return String.format("%0" + length + "d", house_code)
    }

    private fun formatTripLiveBirdRow(num: String, weight: Double, cage: String, house: String): String {
        return String.format(" %3s %7.02f  %4s %2s ", num, weight, cage, house)
    }

    private fun formatTripDeadBirdRow(num: String, weight: Double, qty: String): String {
        return String.format(" %3s %7.02f  %4s    ", num, weight, qty)
    }


    suspend fun constructTripPrintout(tripId: Long): String {

        val tripDp = appDb.tripDao().getDpById(tripId)
        val detailList = appDb.tripDetailDao().getAllByTripId(tripId)
        val mortalityList = appDb.tripMortalityDao().getAllByTripId(tripId)

        var s = ""

        s += formatLine("")
        s += formatLine(tripDp.companyName!!)
        s += formatLine(tripDp.locationName)
        s += formatLine("Date: ${tripDp.docDate}")
        s += formatLine("Document: ${tripDp.docType}-${tripDp.docNo}")
        s += formatLine("Grade: ${tripDp.type}")
        s += formatLine("Truck Code: ${tripDp.truckCode}")
        s += formatLine("BTA Code: ${tripDp.catchBtaCode}")
        s += formatLine("Total Qty: ${tripDp.ttlQty ?: ""}")
        s += formatLine("")

        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine("      LIVE BIRD") + "|" + halfLine("      LIVE BIRD"))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine(TRIP_LIVE_BIRD_HEADER) + "|" + halfLine(TRIP_LIVE_BIRD_HEADER))


        val liveRowNum = Math.ceil(detailList.size / 2.00).toInt()
        val isLiveOdd = (detailList.size % 2) == 1

        var ttlLiveWeight = 0.00

        for ((i, detail) in detailList.withIndex()) {
            if (i < liveRowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine = halfLine(formatTripLiveBirdRow(num, detail.weight!!, detail.cage.toString(), detail.houseCode.toString()))
                ttlLiveWeight += detail.weight!!

                var rightLine = ""
                if (i != (liveRowNum - 1) || !isLiveOdd) {
                    val i2 = i + liveRowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(formatTripLiveBirdRow(num2, detailList[i2].weight!!, detailList[i2].cage.toString(), detail.houseCode.toString()))
                    ttlLiveWeight += detailList[i2].weight!!
                }
                s += formatLine("$leftLine|$rightLine")
            }
        }
        s += formatLine(halfLine("") + "|" + halfLine(""))

        s += formatLine(halfLine(String.format(" WGT: %.2fkg", ttlLiveWeight)))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))

        s += formatLine("")
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine("      DEAD BIRD") + "|" + halfLine("      DEAD BIRD"))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine(TRIP_DEAD_BIRD_HEADER) + "|" + halfLine(TRIP_DEAD_BIRD_HEADER))

        val deadRowNum = Math.ceil(mortalityList.size / 2.00).toInt()
        val isDeadOdd = (mortalityList.size % 2) == 1


        var ttlDeadWeight = 0.00
        var ttlDeadQty = 0

        for ((i, mortality) in mortalityList.withIndex()) {
            if (i < deadRowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine = halfLine(formatTripDeadBirdRow(num, mortality.weight!!, mortality.qty.toString()))
                ttlDeadWeight += mortality.weight!!
                ttlDeadQty += mortality.qty!!


                var rightLine = ""
                if (i != (deadRowNum - 1) || !isDeadOdd) {
                    val i2 = i + deadRowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(formatTripDeadBirdRow(num2, mortalityList[i2].weight!!, mortalityList[i2].qty.toString()))
                    ttlDeadWeight += mortalityList[i2].weight!!
                    ttlDeadQty += mortalityList[i2].qty!!
                }
                s += formatLine("$leftLine|$rightLine")
            }
        }
        s += formatLine(halfLine("") + "|" + halfLine(""))

        s += formatLine(halfLine(String.format(" WGT: %.2fkg", ttlDeadWeight)) + "|" + halfLine(String.format(" QTY: %d", ttlDeadQty)))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine("")

        s += formatLine("Date: " + Sdf.formatDisplay(Calendar.getInstance().time))
        s += formatLine("Time: " + Sdf.formatTime(Calendar.getInstance().time))

        s += formatLine("Ver : " + context.packageManager.getPackageInfo(context.packageName, 0).versionName)
        if (tripDp.isUpload == 1) {
            s += formatLine("Uploaded : Yes")
        }
        if (tripDp.isDelete == 1) {
            s += formatLine("Deleted  : Yes")
        }
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")

        return s
    }

    suspend fun constructOperationPrintout(operationId: Long): String {

        val operation = appDb.operationDao().getById(operationId)
        val mortalityList = appDb.operationMortalityDao().getAllByOperationId(operationId)

        var s = ""

        s += formatLine("")
        s += formatLine("SLAUGHTER HOUSE OPERATION MORTALITY")
        s += formatLine("Document: ${operation.docNo}")
        s += formatLine("Remark: ${operation.remark}")
        s += formatLine("ID: ${operation.docId}")

        s += formatLine("")
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine(OPERATION_HEADER) + "|" + halfLine(OPERATION_HEADER))

        val rowNum = Math.ceil(mortalityList.size / 2.00).toInt()
        val isOdd = (mortalityList.size % 2) == 1

        var ttlWeight = 0.00
        var ttlQty = 0

        for ((i, mortality) in mortalityList.withIndex()) {
            if (i < rowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine = halfLine(formatTripDeadBirdRow(num, mortality.weight!!, mortality.qty.toString()))
                ttlWeight += mortality.weight!!
                ttlQty += mortality.qty!!


                var rightLine = ""
                if (i != (rowNum - 1) || !isOdd) {
                    val i2 = i + rowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(formatTripDeadBirdRow(num2, mortalityList[i2].weight!!, mortalityList[i2].qty.toString()))
                    ttlWeight += mortalityList[i2].weight!!
                    ttlQty += mortalityList[i2].qty!!
                }
                s += formatLine("$leftLine|$rightLine")
            }
        }

        s += formatLine(halfLine("") + "|" + halfLine(""))

        s += formatLine(halfLine(String.format(" WGT: %.2fkg", ttlWeight)) + "|" + halfLine(String.format(" QTY: %d", ttlQty)))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine("")

        s += formatLine("Date: " + Sdf.formatDisplay(Calendar.getInstance().time))
        s += formatLine("Time: " + Sdf.formatTime(Calendar.getInstance().time))

        s += formatLine("Ver : " + context.packageManager.getPackageInfo(context.packageName, 0).versionName)
        if (operation.isUpload == 1) {
            s += formatLine("Uploaded : Yes")
        }
        if (operation.isDelete == 1) {
            s += formatLine("Deleted  : Yes")
        }
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")

        return s
    }
}