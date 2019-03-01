package my.com.engpeng.epslaughterhouse.model

class ServerUrl {
    companion object {

        private const val DEFAULT_GLOBAL_URL = "http://epgroup.dlinkddns.com:5030/eperp/"
        private const val DEFAULT_LOCAL_URL = "http://192.168.8.1:8833/eperp/"

        fun getGlobal(): String {
            return DEFAULT_GLOBAL_URL
        }

        fun getLocal(): String {
            return DEFAULT_LOCAL_URL
        }
    }
}