package my.com.engpeng.epslaughterhouse.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Sdf {
    companion object {
        @SuppressLint("ConstantLocale")
        private val sdfDisplay = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        fun formatDisplay(date: Date): String {
            return sdfDisplay.format(date)
        }

        private val sdfSave = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fun formatSave(date: Date): String {
            return sdfSave.format(date)
        }
        fun formatDisplayFromSave(str: String): String{
            return sdfDisplay.format(sdfSave.parse(str).time)
        }
    }
}