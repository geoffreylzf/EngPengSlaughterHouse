package my.com.engpeng.epslaughterhouse.model

import java.net.Socket


private val PRINTER_COMMAND_PAPER_CUT = byteArrayOf(0x1d, 0x56, 1)

data class NetworkPrinter(
        val ip: String,
        val port: Int) {

    fun testPrint() {
        val sock = Socket(ip, port)
        val oStream = sock.getOutputStream()
        oStream.write("This is came from Android\n\n\n\n\n".toByteArray())
        oStream.write("This is came from Android\n\n\n\n\n".toByteArray())
        oStream.write("This is came from Android\n\n\n\n\n".toByteArray())
        oStream.write(PRINTER_COMMAND_PAPER_CUT)
        sock.close()
    }
}