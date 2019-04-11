package my.com.engpeng.epslaughterhouse.di

import android.content.Context
import android.content.SharedPreferences
import my.com.engpeng.epslaughterhouse.BuildConfig
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.NetworkPrinter
import my.com.engpeng.epslaughterhouse.model.User
import java.util.*

private const val P_KEY_WEIGHING_NAME = "P_KEY_WEIGHING_NAME"
private const val P_KEY_WEIGHING_ADDRESS = "P_KEY_WEIGHING_ADDRESS"
private const val P_KEY_PRINTER_NAME = "P_KEY_PRINTER_NAME"
private const val P_KEY_PRINTER_ADDRESS = "P_KEY_PRINTER_ADDRESS"
private const val P_KEY_USERNAME = "P_KEY_USERNAME"
private const val P_KEY_PASSWORD = "P_KEY_PASSWORD"
private const val P_KEY_UNIQUE_ID = "P_KEY_UNIQUE_ID"
private const val P_KEY_IS_LOCAL = "P_KEY_IS_LOCAL"
private const val P_KEY_NETWORK_PRINTER_IP = "P_KEY_NETWORK_PRINTER_IP"
private const val P_KEY_NETWORK_PRINTER_PORT = "P_KEY_NETWORK_PRINTER_PORT"

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

    fun generateSaveUniqueId() {
        val uniqueID = UUID.randomUUID().toString()
        sharedPreferences.edit().apply {
            putString(P_KEY_UNIQUE_ID, uniqueID)
        }.apply()
    }

    fun getUniqueId(): String {
        var uniqueId: String
        sharedPreferences
                .apply {
                    uniqueId = getString(P_KEY_UNIQUE_ID, "")!!
                }
        return uniqueId
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

    fun saveIsLocal(b: Boolean) {
        sharedPreferences
                .edit()
                .apply {
                    putBoolean(P_KEY_IS_LOCAL, b)
                }
                .apply()
    }

    fun getIsLocal(): Boolean {
        return sharedPreferences.getBoolean(P_KEY_IS_LOCAL, false)
    }

    fun saveNetworkPrinter(np: NetworkPrinter) {
        sharedPreferences
                .edit()
                .apply {
                    putString(P_KEY_NETWORK_PRINTER_IP, np.ip)
                    putInt(P_KEY_NETWORK_PRINTER_PORT, np.port)
                }
                .apply()
    }

    fun getNetworkPrinter(): NetworkPrinter {
        var id: String?
        var port: Int?
        sharedPreferences
                .apply {
                    id = getString(P_KEY_NETWORK_PRINTER_IP, "")
                    port = getInt(P_KEY_NETWORK_PRINTER_PORT, 0)
                }
        return NetworkPrinter(id!!, port!!)
    }
}