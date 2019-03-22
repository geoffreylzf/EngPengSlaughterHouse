package my.com.engpeng.epslaughterhouse.fragment.trip


import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_trip_head.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.camera.CameraPermission
import my.com.engpeng.epslaughterhouse.camera.ScanActivity
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.CompanyDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.DatePickerDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.LocationDialogFragment
import my.com.engpeng.epslaughterhouse.model.ScanData
import my.com.engpeng.epslaughterhouse.model.Trip
import my.com.engpeng.epslaughterhouse.util.I_KEY_SCAN_TEXT
import my.com.engpeng.epslaughterhouse.util.SCAN_REQUEST_CODE
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TripHeadFragment : Fragment() {

    private var calendarDocDate = Calendar.getInstance()
    private var trip = Trip()

    private val vm: TripHeadViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_trip_head, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        vm.liveIsQrScan.observe(this, androidx.lifecycle.Observer {
            freezeEntry(!it) //if true,
        })
        vm.liveCompany.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                trip.companyId = it.id
                et_company.setText(it.companyName)
            } else {
                trip.companyId = null
                et_company.setText("")
            }
        })
        vm.liveLocation.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                trip.locationId = it.id
                et_location.setText(it.locationName)
            } else {
                trip.locationId = null
                et_location.setText("")
            }
        })
        vm.liveCalendar.observe(this, androidx.lifecycle.Observer {
            et_doc_date.setText(Sdf.formatDisplay(it.time))
            trip.docDate = Sdf.formatSave(it.time)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.trip_head, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_history -> {
                findNavController().navigate(TripHeadFragmentDirections.actionTripHeadFragmentToTripHistoryFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        et_company.setOnClickListener {
            showCompanyDialog()
        }

        et_location.setOnClickListener {
            trip.companyId?.run {
                showLocationDialog(this)
            } ?: run {
                AlertDialogFragment.show(fragmentManager!!,
                        "Error",
                        "Please select company first")
            }
        }

        vm.setCalendar(calendarDocDate)

        et_doc_date.setOnClickListener {
            DatePickerDialogFragment
                    .show(fragmentManager!!, calendarDocDate, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        calendarDocDate.set(Calendar.YEAR, year)
                        calendarDocDate.set(Calendar.MONTH, monthOfYear)
                        calendarDocDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        vm.setCalendar(calendarDocDate)
                    })
        }

        btn_start.setOnClickListener {
            start()
        }

        fab_scan.setOnClickListener {
            if (CameraPermission.check(requireActivity())) {
                startActivityForResult(Intent(context, ScanActivity::class.java), SCAN_REQUEST_CODE)
            } else {
                CameraPermission.request(requireActivity())
            }
        }

        fab_refresh.setOnClickListener {
            vm.loadCompany(0)
            vm.loadLocation(0)
            calendarDocDate = Calendar.getInstance()
            vm.setCalendar(calendarDocDate)
            vm.setIsQrScan(false)

            et_doc_no.text?.clear()
            et_truck_code.text?.clear()
            et_catch_bta_code.setText("")
            et_ttl_qty.setText("")
            et_house_list.setText("")

            rg_doc_type.clearCheck()
            rg_type.clearCheck()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val scanText = data?.getStringExtra(I_KEY_SCAN_TEXT) ?: ""
                if (scanText.isNotEmpty()) {
                    try {

                        val scanData = ScanData(scanText)

                        vm.loadCompany(scanData.companyId!!)
                        vm.loadLocation(scanData.locationId!!)

                        calendarDocDate.time = Sdf.getDateFromSave(scanData.docDate!!)
                        vm.setCalendar(calendarDocDate)

                        et_doc_no.setText(scanData.docNo)
                        when (scanData.docType) {
                            "IFT" -> rb_doc_type_ift.isChecked = true
                            "PL" -> rb_doc_type_pl.isChecked = true
                        }
                        when (scanData.type) {
                            "KFC" -> rb_type_kfc.isChecked = true
                            "A" -> rb_type_a.isChecked = true
                            "B" -> rb_type_b.isChecked = true
                        }
                        et_truck_code.setText(scanData.truckCode)
                        et_catch_bta_code.setText(scanData.catchBtaCode)
                        et_house_list.setText(scanData.houseStr)
                        et_ttl_qty.setText(scanData.ttlQty.toString())
                        vm.setIsQrScan(true)

                    } catch (e: Exception) {
                        AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.error_desc, e.message))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun start() {
        trip.run {
            docNo = et_doc_no.text.toString()
            truckCode = et_truck_code.text.toString()

            rg_doc_type.checkedRadioButtonId.let {
                docType = when (it) {
                    R.id.rb_doc_type_ift -> "IFT"
                    R.id.rb_doc_type_pl -> "PL"
                    else -> ""
                }
            }

            rg_type.checkedRadioButtonId.let {
                type = when (it) {
                    R.id.rb_type_kfc -> "KFC"
                    R.id.rb_type_a -> "A"
                    R.id.rb_type_b -> "B"
                    else -> ""
                }
            }

            catchBtaCode = et_catch_bta_code.text.toString().trim()
            houseStr = et_house_list.text.toString().trim()
            ttlQty = et_ttl_qty.text.toString().toIntOrNull()
        }

        var message = ""
        trip.run check@{
            if (companyId == null) {
                message = "Please select company"
                return@check
            }
            if (locationId == null) {
                message = "Please select location"
                return@check
            }
            if (docNo == null || docNo!!.isEmpty()) {
                message = "Please enter document number"
                return@check
            }
            if (docType == null || docType!!.isEmpty()) {
                message = "Please select document type"
                return@check
            }
            if (type == null || type!!.isEmpty()) {
                message = "Please select type"
                return@check
            }
            if (truckCode == null || truckCode!!.isEmpty()) {
                message = "Please enter truck code"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            return
        }

        activity?.hideKeyboard()
        findNavController().navigate(TripHeadFragmentDirections.actionTripHeadFragmentToTripSumFragment(trip))
    }

    private fun showCompanyDialog() {
        CompanyDialogFragment
                .show(fragmentManager!!, object : CompanyDialogFragment.Listener {
                    override fun onSelected(companyId: Long) {
                        vm.loadCompany(companyId)
                        vm.loadLocation(0)
                        showLocationDialog(companyId)
                    }
                })
    }

    private fun showLocationDialog(companyId: Long) {
        LocationDialogFragment
                .show(fragmentManager!!, companyId, object : LocationDialogFragment.Listener {
                    override fun onSelected(locationId: Long) {
                        vm.loadLocation(locationId)
                    }
                })
    }

    private fun freezeEntry(b: Boolean) {
        et_company.isEnabled = b
        et_location.isEnabled = b
        et_doc_date.isEnabled = b
        et_doc_no.isEnabled = b
        et_truck_code.isEnabled = b

        rb_doc_type_ift.isEnabled = b
        rb_doc_type_pl.isEnabled = b

        rb_type_a.isEnabled = b
        rb_type_b.isEnabled = b
        rb_type_kfc.isEnabled = b
    }
}
