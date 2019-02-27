package my.com.engpeng.epslaughterhouse.util

import android.content.Context
import my.com.engpeng.epslaughterhouse.model.SlaughterDetail
import my.com.engpeng.epslaughterhouse.model.SlaughterDisplay
import my.com.engpeng.epslaughterhouse.model.SlaughterMortality
import java.util.*

class PrintUtils {

    companion object {
        private const val PRINT_END = "\n\n\n\n\n"
        private const val PRINT_SEPERATOR = "---------------------------------------------"
        private const val PRINT_HALF_SEPERATOR = "----------------------"
        private const val LINE_CHAR_COUNT = 45

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

        private fun formatTripLiveBirdRow(num: String, weight: Double, qty: String, cover: Int): String {
            return String.format(" %3s %7.02f  %4s  %1s ", num, weight, qty, if (cover == 0) " " else cover.toString())
        }

        private fun formatTripDeadBirdRow(num: String, weight: Double, qty: String): String {
            return String.format(" %3s %7.02f  %4s    ", num, weight, qty)
        }

        private const val TRIP_LIVE_BIRD_HEADER = "  #   Weight   Qty  C "
        private const val TRIP_DEAD_BIRD_HEADER = "  #   Weight   Qty    "

        fun constructTripPrintout(context : Context, slaughterDp: SlaughterDisplay, detailList: List<SlaughterDetail>, mortalityList: List<SlaughterMortality>): String {
            var s = ""

            s += formatLine("")
            s += formatLine(slaughterDp.companyName!!)
            s += formatLine(slaughterDp.locationName)
            s += formatLine("Date: ${slaughterDp.docDate}")
            s += formatLine("Document: ${slaughterDp.docType}-${slaughterDp.docNo}")
            s += formatLine("Grade: ${slaughterDp.type}")
            s += formatLine("Truck Code: ${slaughterDp.truckCode}")
            s += formatLine("")

            s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
            s += formatLine(halfLine("      LIVE BIRD") + "|" + halfLine("      LIVE BIRD"))
            s += formatLine(halfLine(PRINT_HALF_SEPERATOR) + "|" + halfLine(PRINT_HALF_SEPERATOR))
            s += formatLine(halfLine(TRIP_LIVE_BIRD_HEADER) + "|" + halfLine(TRIP_LIVE_BIRD_HEADER))


            val liveRowNum = Math.ceil(detailList.size / 2.00).toInt()
            val isLiveOdd = (detailList.size % 2) == 1

            var ttlLiveWeight = 0.00
            var ttlLiveQty = 0

            for ((i, detail) in detailList.withIndex()) {
                if (i < liveRowNum) {

                    val num = formatNumber(3, i + 1)
                    val leftLine = halfLine(formatTripLiveBirdRow(num, detail.weight!!, detail.qty.toString(), detail.cover!!))
                    ttlLiveWeight += detail.weight!!
                    ttlLiveQty += detail.qty!!

                    var rightLine = ""
                    if (i != (liveRowNum - 1) || !isLiveOdd) {
                        val i2 = i + liveRowNum
                        val num2 = formatNumber(3, i2 + 1)
                        rightLine = halfLine(formatTripLiveBirdRow(num2, detailList[i2].weight!!, detailList[i2].qty.toString(), detailList[i2].cover!!))
                        ttlLiveWeight += detailList[i2].weight!!
                        ttlLiveQty += detailList[i2].qty!!
                    }
                    s += formatLine("$leftLine|$rightLine")
                }
            }
            s += formatLine(halfLine("") + "|" + halfLine(""))

            s += formatLine(halfLine(String.format(" WGT: %.2fkg", ttlLiveWeight)) + "|" + halfLine(String.format(" QTY: %d", ttlLiveQty)))
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
            s += formatLine("-")
            s += formatLine("-")
            s += formatLine("-")
            s += formatLine("-")
            s += formatLine("-")

            return s
        }
    }


}