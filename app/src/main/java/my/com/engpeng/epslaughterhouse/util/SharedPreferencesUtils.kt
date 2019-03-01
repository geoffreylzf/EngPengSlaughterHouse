package my.com.engpeng.epslaughterhouse.util

import android.content.Context
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.User

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

        private const val P_KEY_PRINTER_NAME = "P_KEY_PRINTER_NAME"
        private const val P_KEY_PRINTER_ADDRESS = "P_KEY_PRINTER_ADDRESS"

        fun savePrinterBluetooth(context: Context, wb: Bluetooth) {
            AppModule.providePreferences(context)
                    .edit()
                    .apply {
                        putString(P_KEY_PRINTER_NAME, wb.name)
                        putString(P_KEY_PRINTER_ADDRESS, wb.address)
                    }
                    .apply()
        }

        fun getPrinterBluetooth(context: Context): Bluetooth {
            var name: String?
            var address: String?
            AppModule.providePreferences(context)
                    .apply {
                        name = getString(P_KEY_PRINTER_NAME, "")
                        address = getString(P_KEY_PRINTER_ADDRESS, "")
                    }
            return Bluetooth(name!!, address!!)
        }

        private const val P_KEY_USERNAME = "P_KEY_USERNAME"
        private const val P_KEY_PASSWORD = "P_KEY_PASSWORD"

        fun saveUser(context: Context, u: User) {
            AppModule.providePreferences(context)
                    .edit()
                    .apply {
                        putString(P_KEY_USERNAME, u.username)
                        putString(P_KEY_PASSWORD, u.password)
                    }
                    .apply()
        }

        fun getUser(context: Context): User {
            var username: String?
            var password: String?
            AppModule.providePreferences(context)
                    .apply {
                        username = getString(P_KEY_USERNAME, "")
                        password = getString(P_KEY_PASSWORD, "")
                    }
            return User(username!!, password!!)
        }

        fun removeUser(context: Context) {
            AppModule.providePreferences(context)
                    .edit()
                    .apply {
                        remove(P_KEY_USERNAME)
                        remove(P_KEY_PASSWORD)
                    }.apply()
        }
    }
}