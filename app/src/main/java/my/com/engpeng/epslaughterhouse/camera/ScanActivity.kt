package my.com.engpeng.epslaughterhouse.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import io.reactivex.subjects.BehaviorSubject
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
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
        ScanBus.scanSubject.onNext("${rawResult?.text}")
        finish()
    }

    override fun onBackPressed() {
        ScanBus.scanSubject.onNext("")
        super.onBackPressed()
    }
}

class ScanBus {
    companion object {
        var scanSubject = BehaviorSubject.create<String>()
        fun reset() {
            scanSubject = BehaviorSubject.create<String>()
        }
    }
}
