package my.com.engpeng.epslaughterhouse.model

import android.util.Base64

data class User(
        val username: String,
        val password: String) {

    fun getBasicBase64Auth(): String {
        val up = "$username|$password"
        return "Basic " + String(Base64.encode(up.toByteArray(), android.util.Base64.DEFAULT))
    }
}