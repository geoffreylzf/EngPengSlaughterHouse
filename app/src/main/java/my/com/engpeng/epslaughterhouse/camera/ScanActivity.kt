package my.com.engpeng.epslaughterhouse.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import timber.log.Timber

@ExperimentalCoroutinesApi
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
        CoroutineScope(Dispatchers.IO).launch {
            Timber.e("handleResult")
            ScanBus.scanChannel.send("${rawResult?.text}")
        }
        finish()
    }

    override fun onBackPressed() {
        CoroutineScope(Dispatchers.IO).launch {
            ScanBus.scanChannel.send("")
        }
        super.onBackPressed()
    }
}

class ScanBus {
    @ExperimentalCoroutinesApi
    companion object {
        var scanChannel = ConflatedBroadcastChannel<String>()

        fun reset() {
            scanChannel = ConflatedBroadcastChannel()
        }
    }
}
