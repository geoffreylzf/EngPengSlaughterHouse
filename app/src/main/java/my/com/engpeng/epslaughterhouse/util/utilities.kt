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


const val RC_GOOGLE_SIGN_IN = 8888
const val BT_WT_PREFIX_GROSS = "G.W.:"
const val BT_WT_PREFIX_TARE = "T.W.:"
const val BT_WT_PREFIX_NETT = "N.W.:"
const val BT_WT_PREFIX_KG = "kg"

const val NOTIFICATION_CHANNEL_UPLOAD_ID = "UPLOAD"
const val NOTIFICATION_CHANNEL_GET_DOC_ID = "GET_DOC"
const val NOTIFICATION_UPLOAD_ID = 5001
const val NOTIFICATION_GET_DOC_ID = 5002

const val SCAN_REQUEST_CODE = 1

const val LOG_TASK_UPLOAD = "LOG_TASK_UPLOAD"
const val I_KEY_LOCAL = "I_KEY_LOCAL"
const val I_KEY_DATE = "I_KEY_DATE"
const val I_KEY_SCAN_TEXT = "I_KEY_SCAN_TEXT"


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
    }
}

fun Activity.vibrate() {
    if (Build.VERSION.SDK_INT >= 26) {
        (this.getSystemService(VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(500)
    }
}

fun Activity.hideKeyboard() {
    this.currentFocus?.let {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.appVersion(): String {
    return "Version: ${packageManager.getPackageInfo(this.packageName, 0).versionName}"
}

fun View.requestFocusWithKeyboard(activity: Activity?) {
    this.requestFocus()
    activity?.currentFocus?.let {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Double?.format2Decimal(): String {
    return String.format("%.2f", this)
}

fun Int.formatYesNo(): String {
    return when (this) {
        0 -> "No"
        1 -> "Yes"
        else -> "?"
    }
}
