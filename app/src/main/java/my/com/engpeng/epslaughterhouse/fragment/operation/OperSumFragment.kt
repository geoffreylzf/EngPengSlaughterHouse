package my.com.engpeng.epslaughterhouse.fragment.operation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import kotlinx.android.synthetic.main.fragment_oper_sum.*
import kotlinx.android.synthetic.main.list_item_temp_operation_mortality.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.BluetoothDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import my.com.engpeng.epslaughterhouse.model.Operation
import my.com.engpeng.epslaughterhouse.model.OperationMortality
import my.com.engpeng.epslaughterhouse.model.TempOperationMortality
import my.com.engpeng.epslaughterhouse.util.BT_WT_PREFIX_KG
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import my.com.engpeng.epslaughterhouse.util.requestFocusWithKeyboard
import my.com.engpeng.epslaughterhouse.util.vibrate
import org.koin.android.ext.android.inject

class OperSumFragment : Fragment() {

    private lateinit var operation: Operation

    private val appDb: AppDb by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    private val rvAdapter = TempOperationMortalityAdapter()

    private val bt = BluetoothSPP(context)
    private var btName = ""
    private var btAddress = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_oper_sum, container, false)
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

    private fun setupRv() {
        rv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val (tempId, weight) = viewHolder.itemView.tag as Pair<Long, Double>

                ConfirmDialogFragment.show(fragmentManager!!,
                        "Delete swiped data ?",
                        "Weight: ${weight.format2Decimal()}Kg",
                        "DELETE", object : ConfirmDialogFragment.Listener {
                    override fun onPositiveButtonClicked() {
                        CoroutineScope(Dispatchers.IO).launch {
                            appDb.tempOperationMortalityDao().deleteById(tempId)
                        }
                    }

                    override fun onNegativeButtonClicked() {
                        rvAdapter.refresh()
                    }
                })
            }
        }).attachToRecyclerView(rv)

        appDb.tempOperationMortalityDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })
    }

    private fun setupView() {
        operation = OperSumFragmentArgs.fromBundle(arguments!!).operation
        operation.run {
            et_doc_no.setText(docNo)
            tv_doc_id.text = docId.toString()
            et_remark.setText(if (remark!!.isEmpty()) " " else remark)
        }

        et_weight.requestFocusWithKeyboard(requireActivity())

        btn_insert.setOnClickListener {
            insert()
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

        btn_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val count = appDb.tempOperationMortalityDao().getCount()
                withContext(Dispatchers.Main) {
                    if (count != 0) {
                        preSave()
                    } else {
                        AlertDialogFragment.show(fragmentManager!!,
                                getString(R.string.dialog_title_error),
                                getString(R.string.dialog_error_msg_no_detail))
                    }
                }
            }
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

    private fun insert() {
        val tempOperationMortality = TempOperationMortality()
        tempOperationMortality.run {
            weight = et_weight.text.toString().toDoubleOrNull()
            qty = et_qty.text.toString().toIntOrNull()
        }

        var message = ""
        tempOperationMortality.run check@{
            if (weight == null) {
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
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            appDb.tempOperationMortalityDao().insert(tempOperationMortality)
            withContext(Dispatchers.Main) {
                activity?.vibrate()
                et_weight.text?.clear()
                et_weight.requestFocusWithKeyboard(activity)
            }
        }
    }

    private fun preSave() {
        ConfirmDialogFragment.show(fragmentManager!!,
                "Confirm this operation ?",
                "Edit is not allowed after save",
                "Confirm", object : ConfirmDialogFragment.Listener {
            override fun onPositiveButtonClicked() {
                save()
            }

            override fun onNegativeButtonClicked() {}
        })
    }

    private fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val operationId = appDb.operationDao().insert(operation)
                val tempMortalityList = appDb.tempOperationMortalityDao().getAll()
                val mortalityList = OperationMortality.transformFromTempWithOperationId(operationId, tempMortalityList)
                appDb.operationMortalityDao().insert(mortalityList)

                appDb.tempOperationMortalityDao().deleteAll()

                withContext(Dispatchers.Main) {
                    //TODO findNavController().navigate(TripConfFragmentDirections.actionTripConfFragmentToTripPrintFragment(tripId))
                }



            } catch (e: Exception) {
                AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.error_desc, e.message))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        bt.stopService()
    }
}

class TempOperationMortalityAdapter() : RecyclerView.Adapter<TempOperationMortalityAdapter.ItemViewHolder>() {

    private var tempList: List<TempOperationMortality>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_temp_operation_mortality, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        tempList!![position].let { temp ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = temp.weight.format2Decimal()
                li_tv_qty.text = temp.qty.toString()
                tag = Pair(temp.id, temp.weight)

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tempList?.count() ?: 0
    }

    fun setList(tempList: List<TempOperationMortality>) {
        this.tempList = tempList
        this.notifyDataSetChanged()
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}