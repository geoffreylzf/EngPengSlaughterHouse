package my.com.engpeng.epslaughterhouse.util

import android.content.Context
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.model.Bluetooth

class SharedPreferencesUtils {
    companion object {
        private const val P_KEY_WEIGHING_NAME = "P_KEY_WEIGHING_NAME"
        private const val P_KEY_WEIGHING_ADDRESS = "P_KEY_WEIGHING_ADDRESS"
        fun saveWeighingBluetooth(context: Context, wb: Bluetooth) {
            AppModule.providePreferences(context)
                    .edit()
                    .apply {
                        putString(P_KEY_WEIGHING_NAME, wb.name)
                        putString(P_KEY_WEIGHING_ADDRESS, wb.address)
                    }
                    .apply()
        }

        fun getWeighingBluetooth(context: Context): Bluetooth {
            var name: String?
            var address: String?
            AppModule.providePreferences(context)
                    .apply {
                        name = getString(P_KEY_WEIGHING_NAME, "")
                        address = getString(P_KEY_WEIGHING_ADDRESS, "")
                    }
            return Bluetooth(name!!, address!!)
        }
    }
}