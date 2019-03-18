package my.com.engpeng.epslaughterhouse.fragment.trip


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_head.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.camera.CameraPermission
import my.com.engpeng.epslaughterhouse.camera.ScanActivity
import my.com.engpeng.epslaughterhouse.camera.ScanBus
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.CompanyDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.DatePickerDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.LocationDialogFragment
import my.com.engpeng.epslaughterhouse.model.Trip
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 *
 */
class TripHeadFragment : Fragment() {

    private var calendarDocDate = Calendar.getInstance()
    private var slaughter = Trip()
    private var compositeDisposable = CompositeDisposable()

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
                slaughter.companyId = it.id
                et_company.setText(it.companyName)
            } else {
                slaughter.companyId = null
                et_company.setText("")
            }
        })
        vm.liveLocation.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                slaughter.locationId = it.id
                et_location.setText(it.locationName)
            } else {
                slaughter.locationId = null
                et_location.setText("")
            }
        })
        vm.liveCalendar.observe(this, androidx.lifecycle.Observer {
            et_doc_date.setText(Sdf.formatDisplay(it.time))
            slaughter.docDate = Sdf.formatSave(it.time)
        })
    }

    override fun onResume() {
        super.onResume()
        setupObservable()
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
            slaughter.companyId?.run {
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
                startActivity(Intent(context, ScanActivity::class.java))
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
            et_catch_bta_code.setText(" ")

            rg_doc_type.clearCheck()
            rg_type.clearCheck()
        }
    }

    private fun setupObservable() {

        ScanBus.scanSubject
                .subscribeOn(Schedulers.io())
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { scanText ->
                    if (scanText.isNotEmpty()) {
                        val arr = scanText.split("|")
                        if (arr.size == 7 || arr.size == 8) {

                            val companyId = arr[0].toLong()
                            val locationId = arr[1].toLong()
                            val docDate = arr[2]
                            val docNo = arr[3]
                            val docType = arr[4]
                            val type = arr[5]
                            val truckCode = arr[6]
                            val catchBtaCode = if (arr.size == 8) arr[7] else " "

                            vm.loadCompany(companyId)
                            vm.loadLocation(locationId)

                            calendarDocDate.time = Sdf.getDateFromSave(docDate)
                            vm.setCalendar(calendarDocDate)

                            et_doc_no.setText(docNo)
                            when (docType) {
                                "IFT" -> rb_doc_type_ift.isChecked = true
                                "PL" -> rb_doc_type_pl.isChecked = true
                            }
                            when (type) {
                                "KFC" -> rb_type_kfc.isChecked = true
                                "A" -> rb_type_a.isChecked = true
                                "B" -> rb_type_b.isChecked = true
                            }
                            et_truck_code.setText(truckCode)
                            et_catch_bta_code.setText(catchBtaCode)
                            vm.setIsQrScan(true)
                        } else {
                            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), "Invalid QR code")
                        }
                    }
                    ScanBus.reset()
                }.addTo(compositeDisposable)
    }

    private fun start() {
        slaughter.run {
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
        }

        var message = ""
        slaughter.run check@{
            if (companyId == null) {
                message = "Please select company"
                return@check
            }
            if (locationId == null) {
                message = "Please select location"
                return@check
            }
            if (docNo.isEmpty()) {
                message = "Please enter document number"
                return@check
            }
            if (docType.isEmpty()) {
                message = "Please select document type"
                return@check
            }
            if (type.isEmpty()) {
                message = "Please select type"
                return@check
            }
            if (truckCode.isEmpty()) {
                message = "Please enter truck code"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            return
        }

        activity?.hideKeyboard()
        findNavController().navigate(TripHeadFragmentDirections.actionTripHeadFragmentToTripSumFragment(slaughter))
    }

    private fun showCompanyDialog() {
        CompanyDialogFragment
                .getInstance(fragmentManager!!)
                .selectEvent
                .subscribe {
                    vm.loadCompany(it.id!!)
                    vm.loadLocation(0)
                    showLocationDialog(it.id!!)
                }.addTo(compositeDisposable)
    }

    private fun showLocationDialog(companyId: Long) {
        LocationDialogFragment
                .getInstance(fragmentManager!!, companyId)
                .selectEvent
                .subscribe {
                    vm.loadLocation(it.id!!)
                }.addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
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
