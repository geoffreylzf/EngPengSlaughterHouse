package my.com.engpeng.epslaughterhouse.di

import android.content.Context
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.util.Sdf
import java.util.*

class PrintModule(val context: Context, private val appDb: AppDb) {

    companion object {
        private const val PRINT_END = "\n\n\n\n\n"
        private const val PRINT_SEPERATOR = "---------------------------------------------"
        private const val PRINT_HALF_SEPERATOR = "----------------------"
        private const val LINE_CHAR_COUNT = 45

        private const val RECE_LIVE_BIRD_HEADER = "  #    Weight  Cage   "
        private const val RECE_DEAD_BIRD_HEADER = "  #    Weight   Qty   "

        private const val HANG_HEADER = "  #   Weight   Qty    "
        private const val CAGE_WGT = 7.6
        private const val COVER_WGT = 0.4
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
        return String.format(
            "%-22s",
            if (halfLine.length > 22) halfLine.substring(0, 22) else halfLine
        )
    }

    private fun formatNumber(length: Int, house_code: Int): String {
        return String.format("%0" + length + "d", house_code)
    }

    private fun formatReceLiveBirdRow(num: String, weight: Double, cage: String): String {
        return String.format(" %3s %8.02f  %4s   ", num, weight, cage)
    }

    private fun formatReceDeadBirdRow(num: String, weight: Double, qty: String): String {
        return String.format(" %3s %8.02f  %4s   ", num, weight, qty)
    }

    suspend fun constructReceiveQrText(receId: Long): String {
        val receDp = appDb.shReceiveDao().getDpById(receId)
        var s = "v1"

        s += "|" + receDp.uuid
        s += "|" + receDp.docNo

        return s
    }

    suspend fun constructReceivePrintout(receId: Long): String {

        val receDp = appDb.shReceiveDao().getDpById(receId)
        val detailList = appDb.shReceiveDetailDao().getAllByShReceiveId(receId)
        val mortalityList = appDb.shReceiveMortalityDao().getAllByShReceiveId(receId)

        var s = ""

        s += formatLine("")
        s += formatLine("Slaughter House (Kilang Potong) Receipt")
        s += formatLine(receDp.locationName)
        s += formatLine("Date : ${receDp.docDate}")
        s += formatLine("Document : ${receDp.docType}-${receDp.docNo}")
        s += formatLine("Grade : ${receDp.type}")
        s += formatLine("Truck Code : ${receDp.truckCode}")
        s += formatLine("BTA Code : ${receDp.catchBtaCode}")
        s += formatLine("Total Farm Bird Qty  : ${receDp.ttlQty ?: ""}")
        s += formatLine("Total Farm Cage Qty  : ${receDp.ttlCageQty ?: ""}")
        s += formatLine("Total Farm Cover Qty : ${receDp.ttlCoverQty ?: ""}")
        s += formatLine("UUID : ${receDp.uuid ?: ""}")
        s += formatLine("")

        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine("      LIVE BIRD") + "|" + halfLine("      LIVE BIRD"))
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine(RECE_LIVE_BIRD_HEADER) + "|" + halfLine(RECE_LIVE_BIRD_HEADER))


        val liveRowNum = Math.ceil(detailList.size / 2.00).toInt()
        val isLiveOdd = (detailList.size % 2) == 1

        var ttlLiveWeight = 0.00

        for ((i, detail) in detailList.withIndex()) {
            if (i < liveRowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine =
                    halfLine(formatReceLiveBirdRow(num, detail.weight!!, detail.cage.toString()))
                ttlLiveWeight += detail.weight!!

                var rightLine = ""
                if (i != (liveRowNum - 1) || !isLiveOdd) {
                    val i2 = i + liveRowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(
                        formatReceLiveBirdRow(
                            num2,
                            detailList[i2].weight!!,
                            detailList[i2].cage.toString()
                        )
                    )
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
        s += formatLine(halfLine(RECE_DEAD_BIRD_HEADER) + "|" + halfLine(RECE_DEAD_BIRD_HEADER))

        val deadRowNum = Math.ceil(mortalityList.size / 2.00).toInt()
        val isDeadOdd = (mortalityList.size % 2) == 1


        var ttlDeadWeight = 0.00
        var ttlDeadQty = 0

        for ((i, mortality) in mortalityList.withIndex()) {
            if (i < deadRowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine = halfLine(
                    formatReceDeadBirdRow(
                        num,
                        mortality.weight!!,
                        mortality.qty.toString()
                    )
                )
                ttlDeadWeight += mortality.weight!!
                ttlDeadQty += mortality.qty!!


                var rightLine = ""
                if (i != (deadRowNum - 1) || !isDeadOdd) {
                    val i2 = i + deadRowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(
                        formatReceDeadBirdRow(
                            num2,
                            mortalityList[i2].weight!!,
                            mortalityList[i2].qty.toString()
                        )
                    )
                    ttlDeadWeight += mortalityList[i2].weight!!
                    ttlDeadQty += mortalityList[i2].qty!!
                }
                s += formatLine("$leftLine|$rightLine")
            }
        }
        s += formatLine(halfLine("") + "|" + halfLine(""))

        s += formatLine(
            halfLine(String.format(" WGT: %.2fkg", ttlDeadWeight)) + "|" + halfLine(
                String.format(" QTY: %d", ttlDeadQty)
            )
        )
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))


        val avgWgt = ((ttlLiveWeight + ttlDeadWeight)
                - ((receDp.ttlCageQty ?: 0) * CAGE_WGT)
                - ((receDp.ttlCoverQty ?: 0) * COVER_WGT)) / (receDp.ttlQty ?: 1)

        s += formatLine(String.format("AVERAGE WEIGHT : %.2fkg", avgWgt))

        s += formatLine("")
        s += formatLine("Date: " + Sdf.formatDisplay(Calendar.getInstance().time))
        s += formatLine("Time: " + Sdf.formatTime(Calendar.getInstance().time))

        s += formatLine(
            "Ver : " + context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName
        )
        if (receDp.isUpload == 1) {
            s += formatLine("Uploaded : Yes")
        }
        if (receDp.isDelete == 1) {
            s += formatLine("Deleted  : Yes")
        }
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")
        s += formatLine("-")

        return s
    }

    suspend fun constructHangPrintout(hangId: Long): String {

        val hang = appDb.shHangDao().getById(hangId)
        val mortalityList = appDb.shHangMortalityDao().getAllByShHangId(hangId)

        var s = ""

        s += formatLine("")
        s += formatLine("SLAUGHTER HOUSE HANGING MORTALITY")
        s += formatLine("Document : ${hang.docNo}")
        s += formatLine("Remark : ${hang.remark}")
        s += formatLine("ID : ${hang.docId ?: ""}")
        s += formatLine("Rec. UUID : ${hang.shReceiveUuid ?: ""}")

        s += formatLine("")
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine(halfLine(HANG_HEADER) + "|" + halfLine(HANG_HEADER))

        val rowNum = Math.ceil(mortalityList.size / 2.00).toInt()
        val isOdd = (mortalityList.size % 2) == 1

        var ttlWeight = 0.00
        var ttlQty = 0

        for ((i, mortality) in mortalityList.withIndex()) {
            if (i < rowNum) {

                val num = formatNumber(3, i + 1)
                val leftLine = halfLine(
                    formatReceDeadBirdRow(
                        num,
                        mortality.weight!!,
                        mortality.qty.toString()
                    )
                )
                ttlWeight += mortality.weight!!
                ttlQty += mortality.qty!!


                var rightLine = ""
                if (i != (rowNum - 1) || !isOdd) {
                    val i2 = i + rowNum
                    val num2 = formatNumber(3, i2 + 1)
                    rightLine = halfLine(
                        formatReceDeadBirdRow(
                            num2,
                            mortalityList[i2].weight!!,
                            mortalityList[i2].qty.toString()
                        )
                    )
                    ttlWeight += mortalityList[i2].weight!!
                    ttlQty += mortalityList[i2].qty!!
                }
                s += formatLine("$leftLine|$rightLine")
            }
        }

        s += formatLine(halfLine("") + "|" + halfLine(""))

        s += formatLine(
            halfLine(
                String.format(
                    " WGT: %.2fkg",
                    ttlWeight
                )
            ) + "|" + halfLine(String.format(" QTY: %d", ttlQty))
        )
        s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
        s += formatLine("")

        s += formatLine("Date: " + Sdf.formatDisplay(Calendar.getInstance().time))
        s += formatLine("Time: " + Sdf.formatTime(Calendar.getInstance().time))

        s += formatLine(
            "Ver : " + context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName
        )
        if (hang.isUpload == 1) {
            s += formatLine("Uploaded : Yes")
        }
        if (hang.isDelete == 1) {
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