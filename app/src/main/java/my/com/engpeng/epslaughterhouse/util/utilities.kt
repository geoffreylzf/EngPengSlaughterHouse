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

        fun formatDisplayFromSave(str: String): String {
            return sdfDisplay.format(sdfSave.parse(str).time)
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

enum class DocType {
    IFT, PL
}