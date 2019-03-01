package my.com.engpeng.epslaughterhouse.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import my.com.engpeng.epslaughterhouse.R

class UiUtils {
    companion object {
        fun getProgressDialog(context: Context): Dialog {
            return Dialog(context, android.R.style.Theme_Black).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window!!.setBackgroundDrawableResource(R.color.colorTranslucent10)
                setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null))
                setCancelable(false)
            }
        }
    }
}