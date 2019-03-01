package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_mortality.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.TempSlaughterMortality
import my.com.engpeng.epslaughterhouse.util.*

class EnterMortalityDialogFragment : DialogFragment() {
    companion object {
        val TAG = this::class.qualifiedName
        fun getInstance(fm: FragmentManager): EnterMortalityDialogFragment {
            return EnterMortalityDialogFragment().apply {
                show(fm, TAG)
            }
        }
    }

    private val doneSubject = PublishSubject.create<TempSlaughterMortality>()
    private val temp = TempSlaughterMortality()

    val doneEvent: Observable<TempSlaughterMortality> = doneSubject

    private val bt = BluetoothSPP(context)
    private var btName = ""
    private var btAddress = ""

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_mortality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_save.setOnClickListener {
            temp.run{
                weight = et_weight.text.toString().toDoubleOrNull()
                qty = et_qty.text.toString().toIntOrNull()
            }

            var message = ""
            temp.run check@{
                if(weight == null){
                    message = "Please enter weight"
                    return@check
                }
                if (qty == null || qty == 0) {
                    message = "Please enter quantity"
                    return@check
                }
            }

            if (message.isNotEmpty()) {
                AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            }else{
                doneSubject.onNext(temp)
                activity?.vibrate()
                dismiss()
            }
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }

        et_weight.requestFocusWithKeyboard(activity)

        btn_bt_start.setOnClickListener { startWeighingService() }

        btn_weight_scale.setOnClickListener {
            et_weight.setText(btn_weight_scale.text)
        }

        btn_bt.setOnClickListener {
            BluetoothDialogFragment.show(fragmentManager!!,
                    bt.pairedDeviceName.toList(),
                    bt.pairedDeviceAddress.toList(),
                    object : BluetoothDialogFragment.Listener {
                        override fun onSelect(bluetooth: Bluetooth) {
                            SharedPreferencesUtils.saveWeighingBluetooth(context!!, bluetooth)
                            startWeighingBluetooth()
                        }
                    })
        }

    }

    override fun onResume() {
        super.onResume()
        startWeighingBluetooth()
    }

    private fun startWeighingBluetooth() {

        if (!bt.isBluetoothAvailable) {
            tv_bt_status.text = getString(R.string.bt_status, "Not Available")
            return
        }
        if (!bt.isBluetoothEnabled) {
            tv_bt_status.text = getString(R.string.bt_status, "Not Enable")
            return
        }

        SharedPreferencesUtils.getWeighingBluetooth(context!!).run {
            btName = name
            btAddress = address
        }

        tv_bt_status.text = getString(R.string.bt_status, "Not Connected")
        tv_bt_name.text = getString(R.string.bt_name, btName)
        tv_bt_address.text = getString(R.string.bt_address, btAddress)

        if (btName.isNotEmpty() && btAddress.isNotEmpty()) {
            btn_bt_start.isEnabled = true
        }

        bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceDisconnected() {
                tv_bt_status?.text = getString(R.string.bt_status, "Not Connected")
                btn_weight_scale?.visibility = View.INVISIBLE
            }

            override fun onDeviceConnectionFailed() {
                tv_bt_status?.text = getString(R.string.bt_status, "Connection Failed")
                btn_weight_scale?.visibility = View.INVISIBLE
            }

            override fun onDeviceConnected(name: String, address: String) {
                tv_bt_status?.text = getString(R.string.bt_status, "Connected To $address")
                btn_weight_scale?.visibility = View.VISIBLE
            }
        })

        bt.setOnDataReceivedListener { _, message ->
            message.run {
                if (contains(BT_WT_PREFIX_NETT)) {
                    btn_weight_scale?.text = replace(BT_WT_PREFIX_NETT, "")
                            .replace(BT_WT_PREFIX_KG, "")
                            .trim()
                            .toDoubleOrNull()
                            .format2Decimal()
                }
            }
        }

        if (!bt.isServiceAvailable) {
            bt.setupService()
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER)
        }
    }

    override fun onStop() {
        super.onStop()
        bt.stopService()
    }

    private fun startWeighingService() {
        bt.connect(btAddress)
    }
}