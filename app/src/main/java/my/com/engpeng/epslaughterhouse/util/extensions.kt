package my.com.engpeng.epslaughterhouse.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Activity.vibrate() {
    if (Build.VERSION.SDK_INT >= 26) {
        (this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        (this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(500)
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
