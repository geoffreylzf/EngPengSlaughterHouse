package my.com.engpeng.epslaughterhouse.util

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.util.*


private const val HEX_STR = "0123456789ABCDEF"
private const val QR_STRIDE = 500
private const val QR_HALF = 250

class BarcodeUtil(private val text: String) {
    private var qrWidth = 500
    private var qrHeight = 500
    private var barcodeFormat = BarcodeFormat.QR_CODE

    fun convertToQrByteArray(): ByteArray {
        val bmp = this.encodeStringAsQrCodeBitmap(text)
        val bmpTop = this.getTopPartBitmap(bmp)
        val bmpBottom = this.getBottomPartBitmap(bmp)

        val bmpTopByte = this.decodeBitmapAsByteArray(bmpTop)
        val bmpBottomByte = this.decodeBitmapAsByteArray(bmpBottom)

        return bmpTopByte!!.plus(bmpBottomByte!!)
    }

    fun convertToCode128ByteArray(): ByteArray {
        qrWidth = 500
        qrHeight = 150
        barcodeFormat = BarcodeFormat.CODE_128

        val bmp = this.encodeStringAsQrCodeBitmap(text)
        val bmpByte = this.decodeBitmapAsByteArray(bmp)

        return bmpByte!!
    }

    private val binaryArray = arrayOf("0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111")

    private fun encodeStringAsQrCodeBitmap(str: String): Bitmap {
        val result = MultiFormatWriter().encode(str, barcodeFormat, qrWidth, qrHeight)

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, QR_STRIDE, 0, 0, width, height)
        return bitmap
    }

    private fun getTopPartBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(bitmap, 0, 0, qrWidth, QR_HALF)
    }

    private fun getBottomPartBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(bitmap, 0, QR_HALF, qrWidth, QR_HALF)
    }

    private fun decodeBitmapAsByteArray(bmp: Bitmap): ByteArray? {
        val bmpWidth = bmp.width
        val bmpHeight = bmp.height

        val list = ArrayList<String>()
        var sb: StringBuffer

        val zeroCount = bmpWidth % 8

        var zeroStr = ""
        if (zeroCount > 0) {
            for (i in 0 until 8 - zeroCount) {
                zeroStr += "0"
            }
        }

        for (i in 0 until bmpHeight) {
            sb = StringBuffer()
            for (j in 0 until bmpWidth) {
                val color = bmp.getPixel(j, i)

                val r = color shr 16 and 0xff
                val g = color shr 8 and 0xff
                val b = color and 0xff

                if (r > 160 && g > 160 && b > 160)
                    sb.append("0")
                else
                    sb.append("1")
            }
            if (zeroCount > 0) {
                sb.append(zeroStr)
            }
            list.add(sb.toString())
        }

        val bmpHexList = binaryListToHexStringList(list)
        val commandHexString = "1D763000"
        var widthHexString = Integer
                .toHexString(if (bmpWidth % 8 == 0)
                    bmpWidth / 8
                else
                    bmpWidth / 8 + 1)
        if (widthHexString.length > 2) {
            Log.e("decodeBitmap error", " width is too large")
            return null
        } else if (widthHexString.length == 1) {
            widthHexString = "0$widthHexString"
        }
        widthHexString += "00"

        var heightHexString = Integer.toHexString(bmpHeight)
        if (heightHexString.length > 2) {
            Log.e("decodeBitmap error", " height is too large")
            return null
        } else if (heightHexString.length == 1) {
            heightHexString = "0$heightHexString"
        }
        heightHexString += "00"

        val commandList = ArrayList<String>()
        commandList.add(commandHexString + widthHexString + heightHexString)
        commandList.addAll(bmpHexList)

        return hexList2Byte(commandList)
    }

    private fun binaryListToHexStringList(list: List<String>): List<String> {
        val hexList = ArrayList<String>()
        for (binaryStr in list) {
            val sb = StringBuffer()
            var i = 0
            while (i < binaryStr.length) {
                val str = binaryStr.substring(i, i + 8)

                val hexString = myBinaryStrToHexString(str)
                sb.append(hexString)
                i += 8
            }
            hexList.add(sb.toString())
        }
        return hexList

    }

    private fun myBinaryStrToHexString(binaryStr: String): String {
        var hex = ""
        val f4 = binaryStr.substring(0, 4)
        val b4 = binaryStr.substring(4, 8)
        for (i in binaryArray.indices) {
            if (f4 == binaryArray[i])
                hex += HEX_STR.substring(i, i + 1)
        }
        for (i in binaryArray.indices) {
            if (b4 == binaryArray[i])
                hex += HEX_STR.substring(i, i + 1)
        }

        return hex
    }

    private fun hexList2Byte(list: List<String>): ByteArray {
        val commandList = ArrayList<ByteArray>()

        for (hexStr in list) {
            commandList.add(hexStringToBytes(hexStr))
        }
        return sysCopy(commandList)
    }

    private fun hexStringToBytes(str: String): ByteArray {
        val result = ByteArray(str.length / 2)
        for (i in 0 until str.length step 2) {
            result[i / 2] = Integer.valueOf(str.substring(i, i + 2), 16).toByte()
        }
        return result
    }

    private fun sysCopy(srcArrays: List<ByteArray>): ByteArray {
        var len = 0
        for (srcArray in srcArrays) {
            len += srcArray.size
        }
        val destArray = ByteArray(len)
        var destLen = 0
        for (srcArray in srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.size)
            destLen += srcArray.size
        }
        return destArray
    }
}
