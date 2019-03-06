package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_detail.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempSlaughterDetailAdapter
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.BluetoothDialogFragment
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.TempSlaughterDetail
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 *
 */


class TripDetailFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    private val tempSlaughterDetail = TempSlaughterDetail()
    private val rvAdapter = TempSlaughterDetailAdapter(true)
    private val compositeDisposable = CompositeDisposable()

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
        setupView()
        setupRv()
    }

    override fun onResume() {
        super.onResume()
        startWeighingBluetooth()
    }

    private fun setupView() {
        rb_cage_1.setOnClickListener { setupSpinnerCoverQty(1, 1) }
        rb_cage_2.setOnClickListener { setupSpinnerCoverQty(2, 2) }
        rb_cage_3.setOnClickListener { setupSpinnerCoverQty(3, 3) }
        rb_cage_4.setOnClickListener { setupSpinnerCoverQty(4, 4) }
        rb_cage_5.setOnClickListener { setupSpinnerCoverQty(5, 5) }
        rb_cage_6.setOnClickListener { setupSpinnerCoverQty(6, 6) }

        setupSpinnerCoverQty(0, 0)

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
        appDb.tempSlaughterDetailDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })
    }

    private fun setupSpinnerCoverQty(qty: Int, default: Int?) {
        sn_cover.run {
            adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, ArrayList<Int>().apply {
                for (i in 0..qty) {
                    add(i)
                }
            }).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            default?.run {
                setSelection(this)
            }
        }
    }

    private fun save() {
        tempSlaughterDetail.run {
            weight = et_weight.text.toString().toDoubleOrNull()
            qty = et_qty.text.toString().toIntOrNull()

            cage = when (rg_cage.checkedRadioButtonId) {
                R.id.rb_cage_1 -> 1
                R.id.rb_cage_2 -> 2
                R.id.rb_cage_3 -> 3
                R.id.rb_cage_4 -> 4
                R.id.rb_cage_5 -> 5
                R.id.rb_cage_6 -> 6
                else -> 0
            }
            cover = sn_cover.selectedItem.toString().toIntOrNull()
        }

        var message = ""
        tempSlaughterDetail.run check@{
            if (weight == null) {
                message = "Please enter weight"
                return@check
            }
            if (qty == null || qty == 0) {
                message = "Please enter quantity"
                return@check
            }
            if (cage == null || cage == 0) {
                message = "Please select cage"
                return@check
            }
            if (cover == null) {
                message = "Please select cover"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            return
        }

        appDb.tempSlaughterDetailDao()
                .insert(tempSlaughterDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    activity?.vibrate()
                    et_weight.text?.clear()
                    et_weight.requestFocusWithKeyboard(activity)
                }, {}).addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
        bt.stopService()
    }

    private fun backToSummary() {
        activity?.hideKeyboard()
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    findNavController().popBackStack()
                }.addTo(compositeDisposable)
    }
}
