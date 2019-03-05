package my.com.engpeng.epslaughterhouse.di

import android.content.Context
import android.content.SharedPreferences
import my.com.engpeng.epslaughterhouse.BuildConfig
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.User

private const val P_KEY_WEIGHING_NAME = "P_KEY_WEIGHING_NAME"
private const val P_KEY_WEIGHING_ADDRESS = "P_KEY_WEIGHING_ADDRESS"
private const val P_KEY_PRINTER_NAME = "P_KEY_PRINTER_NAME"
private const val P_KEY_PRINTER_ADDRESS = "P_KEY_PRINTER_ADDRESS"
private const val P_KEY_USERNAME = "P_KEY_USERNAME"
private const val P_KEY_PASSWORD = "P_KEY_PASSWORD"

private const val PREFERENCE_FILE_KEY = BuildConfig.APPLICATION_ID

class SharedPreferencesModule(context: Context) {

    var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun saveWeighingBluetooth(wb: Bluetooth) {
        sharedPreferences
                .edit()
                .apply {
                    putString(P_KEY_WEIGHING_NAME, wb.name)
                    putString(P_KEY_WEIGHING_ADDRESS, wb.address)
                }
                .apply()
    }

    fun getWeighingBluetooth(): Bluetooth {
        var name: String?
        var address: String?
        sharedPreferences
                .apply {
                    name = getString(P_KEY_WEIGHING_NAME, "")
                    address = getString(P_KEY_WEIGHING_ADDRESS, "")
                }
        return Bluetooth(name!!, address!!)
    }


    fun savePrinterBluetooth(wb: Bluetooth) {
        sharedPreferences
                .edit()
                .apply {
                    putString(P_KEY_PRINTER_NAME, wb.name)
                    putString(P_KEY_PRINTER_ADDRESS, wb.address)
                }
                .apply()
    }

    fun getPrinterBluetooth(): Bluetooth {
        var name: String?
        var address: String?
        sharedPreferences
                .apply {
                    name = getString(P_KEY_PRINTER_NAME, "")
                    address = getString(P_KEY_PRINTER_ADDRESS, "")
                }
        return Bluetooth(name!!, address!!)
    }


    fun saveUser(u: User) {
        sharedPreferences
                .edit()
                .apply {
                    putString(P_KEY_USERNAME, u.username)
                    putString(P_KEY_PASSWORD, u.password)
                }
                .apply()
    }

    fun getUser(): User {
        var username: String?
        var password: String?
        sharedPreferences
                .apply {
                    username = getString(P_KEY_USERNAME, "")
                    password = getString(P_KEY_PASSWORD, "")
                }
        return User(username!!, password!!)
    }

    fun removeUser() {
        sharedPreferences
                .edit()
                .apply {
                    remove(P_KEY_USERNAME)
                    remove(P_KEY_PASSWORD)
                }.apply()
    }
}