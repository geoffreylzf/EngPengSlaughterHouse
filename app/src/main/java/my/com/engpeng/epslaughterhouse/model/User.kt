package my.com.engpeng.epslaughterhouse.model

import okhttp3.Credentials

data class User(
        val username: String,
        val password: String) {

    val credentials = Credentials.basic(username, password)

    fun isValid(): Boolean {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            return true
        }
        return false
    }
}