package my.com.engpeng.epslaughterhouse.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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

        private val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        fun formatTime(date: Date): String {
            return sdfTime.format(date)
        }

        fun formatDisplayFromSave(str: String): String {
            return sdfDisplay.format(sdfSave.parse(str).time)
        }

        fun getDateFromSave(str: String): Date {
            return sdfSave.parse(str)
        }

        private val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        fun getCurrentDateTime(): String {
            return sdfDateTime.format(Calendar.getInstance().time)
        }

        fun getCurrentDate(): String {
            return sdfSave.format(Calendar.getInstance().time)
        }

        private val sdfBackupDateTimeFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        fun getBackupDatetimeFormat(): String {
            return sdfBackupDateTimeFormat.format(Calendar.getInstance().time)
        }
    }
}