package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_print.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.BluetoothDialogFragment
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.SlaughterDetail
import my.com.engpeng.epslaughterhouse.model.SlaughterDisplay
import my.com.engpeng.epslaughterhouse.model.SlaughterMortality
import my.com.engpeng.epslaughterhouse.util.PrintUtils
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class TripPrintFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    private var compositeDisposable = CompositeDisposable()

    private val bt = BluetoothSPP(context)
    private var btName = ""
    private var btAddress = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trip_print, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constructPrint()

        btn_bt.setOnClickListener {
            BluetoothDialogFragment.show(fragmentManager!!,
                    bt.pairedDeviceName.toList(),
                    bt.pairedDeviceAddress.toList(),
                    object : BluetoothDialogFragment.Listener {
                        override fun onSelect(bluetooth: Bluetooth) {
                            sharedPreferencesModule.savePrinterBluetooth(bluetooth)
                            startPrinterBluetooth()
                        }
                    })
        }

        btn_bt_refresh.setOnClickListener {
            startPrinterBluetooth()
        }

        btn_bt_start.setOnClickListener { bt.send(tv_printout.text.toString(), true) }
    }

    private fun startPrinterBluetooth() {

        if (!bt.isBluetoothAvailable) {
            tv_bt_status.text = getString(R.string.bt_status, "Not Available")
            btn_bt_refresh.isEnabled = false
            return
        }
        if (!bt.isBluetoothEnabled) {
            tv_bt_status.text = getString(R.string.bt_status, "Not Enable")
            btn_bt_refresh.isEnabled = false
            return
        }

        btn_bt_refresh.isEnabled = true

        sharedPreferencesModule.getPrinterBluetooth().run {
            btName = name
            btAddress = address
        }

        tv_bt_status.text = getString(R.string.bt_status, "Not Connected")
        tv_bt_name.text = getString(R.string.bt_name, btName)
        tv_bt_address.text = getString(R.string.bt_address, btAddress)

        bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceDisconnected() {
                tv_bt_status?.text = getString(R.string.bt_status, "Not Connected")
                btn_bt_start?.isEnabled = false
            }

            override fun onDeviceConnectionFailed() {
                tv_bt_status?.text = getString(R.string.bt_status, "Connection Failed")
                btn_bt_start?.isEnabled = false
            }

            override fun onDeviceConnected(name: String, address: String) {
                tv_bt_status?.text = getString(R.string.bt_status, "Connected To $address")
                btn_bt_start?.isEnabled = true
            }
        })

        bt.stopService()

        if (!bt.isServiceAvailable) {
            bt.setupService()
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER)
        }

        if (btName.isNotEmpty() && btAddress.isNotEmpty()) {
            Maybe.timer(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        bt.connect(btAddress)
                    }.addTo(compositeDisposable)
        }
    }

    private fun constructPrint() {
        val slaughterId = TripHistoryDetailFragmentArgs.fromBundle(arguments!!).slaughterId
        var slaughterDp: SlaughterDisplay? = null
        var slaughterDetailList: List<SlaughterDetail>? = null
        var slaughterMortalityList: List<SlaughterMortality>? = null

        appDb.slaughterDao().getDpById(slaughterId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    slaughterDp = it
                }
                .flatMapMaybe {
                    appDb.slaughterDetailDao().getAllBySlaughterId(slaughterId)
                            .doOnSuccess {
                                slaughterDetailList = it
                            }
                }
                .flatMap {
                    appDb.slaughterMortalityDao().getAllBySlaughterId(slaughterId)
                            .doOnSuccess {
                                slaughterMortalityList = it
                            }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tv_printout.text = PrintUtils.constructTripPrintout(context!!, slaughterDp!!, slaughterDetailList!!, slaughterMortalityList!!)
                }, {})
                .addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
        bt.stopService()
    }

    override fun onResume() {
        super.onResume()
        startPrinterBluetooth()
    }

}
