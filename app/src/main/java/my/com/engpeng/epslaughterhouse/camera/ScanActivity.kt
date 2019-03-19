package my.com.engpeng.epslaughterhouse.camera

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import me.dm7.barcodescanner.zxing.ZXingScannerView


const val SCAN_REQUEST_CODE = 1
const val I_KEY_SCAN = "I_KEY_SCAN"

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        scannerView?.flash = true
        title = "Scan QR"
        setContentView(scannerView)
    }

    override fun onResume() {
        super.onResume()
        scannerView!!.setResultHandler(this)
        scannerView!!.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView!!.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView!!.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        setResult(RESULT_OK, Intent().apply { putExtra(I_KEY_SCAN, rawResult?.text) })
        finish()
    }
}
