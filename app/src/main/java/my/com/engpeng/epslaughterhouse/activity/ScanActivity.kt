package my.com.engpeng.epslaughterhouse.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import my.com.engpeng.epslaughterhouse.bus.RxBus

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var zXingScannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zXingScannerView = ZXingScannerView(this)
        setContentView(zXingScannerView)
    }

    override fun onResume() {
        super.onResume()
        zXingScannerView!!.setResultHandler(this)
        zXingScannerView!!.startCamera()
    }

    override fun onPause() {
        super.onPause()
        zXingScannerView!!.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        RxBus.behaviorSubject.onNext("${rawResult?.text}")
        onBackPressed()
    }
}
