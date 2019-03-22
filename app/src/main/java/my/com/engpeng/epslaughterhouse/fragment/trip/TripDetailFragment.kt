package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.android.synthetic.main.fragment_trip_detail.*
import kotlinx.coroutines.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempTripDetailAdapter
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.BluetoothDialogFragment
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.TempTripDetail
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject


/**
 * A simple [Fragment] subclass.
 *
 */


class TripDetailFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()
    private lateinit var houseStr: String

    private val tempTripDetail = TempTripDetail()
    private val rvAdapter = TempTripDetailAdapter(true)

    private val bt = BluetoothSPP(context)
    private var btName = ""
    private var btAddress = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseStr = TripDetailFragmentArgs.fromBundle(arguments!!).houseStr
        setupRb()
        setupView()
        setupRv()
    }

    override fun onResume() {
        super.onResume()
        startWeighingBluetooth()
    }

    private fun setupRb() {
        if (houseStr.isNotEmpty()) {
            til_house_code.visibility = View.GONE
            val houseList = houseStr.split(",").map { it.toInt() }
            for (house in houseList) {
                rg_house_code.addView(MaterialRadioButton(context).apply {
                    id = house
                    text = getString(R.string.house_desc, house)
                })
            }
        } else {
            tv_house_code.visibility = View.GONE
            rg_house_code.visibility = View.GONE
        }
    }

    private fun setupView() {

        et_weight.requestFocusWithKeyboard(requireActivity())

        btn_save.setOnClickListener { save() }

        btn_cancel.setOnClickListener {
            backToSummary()
        }

        rv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

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
                            sharedPreferencesModule.saveWeighingBluetooth(bluetooth)
                            startWeighingBluetooth()
                        }
                    })
        }
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

        sharedPreferencesModule.getWeighingBluetooth().run {
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
                tv_bt_status?.text = getString(R.string.bt_status, "Connected")
                btn_weight_scale?.visibility = View.VISIBLE
            }
        })

        bt.setOnDataReceivedListener { _, message ->
            message.run {
                if (contains(BT_WT_PREFIX_KG)) {
                    btn_weight_scale?.text = replace(BT_WT_PREFIX_KG, "")
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

    private fun startWeighingService() {
        bt.connect(btAddress)
    }

    private fun setupRv() {
        appDb.tempTripDetailDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })
    }

    private fun save() {
        tempTripDetail.run {
            weight = et_weight.text.toString().toDoubleOrNull()
            cage = et_cage.text.toString().toIntOrNull()

            houseCode = if (houseStr.isNotEmpty()) {
                rg_house_code.checkedRadioButtonId
            } else {
                et_house_code.text.toString().toIntOrNull()
            }
        }

        var message = ""
        tempTripDetail.run check@{
            if (weight == 0.0 || weight == null) {
                message = "Please enter weight"
                return@check
            }
            if (cage == 0 || cage == null) {
                message = "Please enter cage"
                et_cage.requestFocus()
                return@check
            }
            if (houseCode == null || houseCode!! <= 0) {
                message = if (houseStr.isEmpty()) {
                    et_house_code.requestFocus()
                    "Please enter house code"
                }else{
                    "Please select house code"
                }
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            appDb.tempTripDetailDao().insert(tempTripDetail)
            withContext(Dispatchers.Main) {
                activity?.vibrate()
                et_weight.text?.clear()
                et_weight.requestFocusWithKeyboard(activity)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        bt.stopService()
    }

    private fun backToSummary() {
        activity?.hideKeyboard()
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            withContext(Dispatchers.Main) {
                findNavController().popBackStack()
            }
        }
    }
}
