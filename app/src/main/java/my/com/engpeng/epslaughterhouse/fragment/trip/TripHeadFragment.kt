package my.com.engpeng.epslaughterhouse.fragment.trip


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_head.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.camera.CameraPermission
import my.com.engpeng.epslaughterhouse.camera.ScanActivity
import my.com.engpeng.epslaughterhouse.camera.ScanBus
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.CompanyDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.DatePickerDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.LocationDialogFragment
import my.com.engpeng.epslaughterhouse.model.CompanyOption
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 *
 */
class TripHeadFragment : Fragment() {

    private val appDb: AppDb by inject()

    private var calendarDocDate = Calendar.getInstance()
    private var slaughter = Slaughter()
    private val companySubject = PublishSubject.create<CompanyOption>()
    private val locationSubject = PublishSubject.create<Long>()
    private var compositeDisposable = CompositeDisposable()

    private val vm: TripHeadViewModel by lazy { ViewModelProviders.of(this).get(TripHeadViewModel::class.java) }

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
            when (it) {
                true -> freezeEntry()
                false -> unfreezeEntry()
            }
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

        et_doc_date.setText(Sdf.formatDisplay(calendarDocDate.time))
        slaughter.docDate = Sdf.formatSave(calendarDocDate.time)

        et_doc_date.setOnClickListener {
            DatePickerDialogFragment
                    .show(fragmentManager!!, calendarDocDate, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        calendarDocDate.set(Calendar.YEAR, year)
                        calendarDocDate.set(Calendar.MONTH, monthOfYear)
                        calendarDocDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        et_doc_date.setText(Sdf.formatDisplay(calendarDocDate.time))
                        slaughter.docDate = Sdf.formatSave(calendarDocDate.time)
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
            vm.setIsQrScan(false)
        }
    }

    private fun setupObservable() {

        companySubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (!it.isShowLocation) {
                        slaughter.locationId = null
                        et_location.text?.clear()
                        showLocationDialog(it.id)
                    }
                }
                .observeOn(Schedulers.io())
                .flatMapSingle { appDb.companyDao().getById(it.id) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        slaughter.companyId = id
                        et_company.setText(companyName)
                    }
                }, {
                    slaughter.companyId = null
                    et_company.text?.clear()
                }).addTo(compositeDisposable)

        locationSubject.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle { appDb.locationDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.run {
                        slaughter.locationId = id
                        et_location.setText(locationName)
                    }
                }, {
                    slaughter.locationId = null
                    et_location.text?.clear()
                }).addTo(compositeDisposable)

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

                            companySubject.onNext(CompanyOption(companyId, true))
                            locationSubject.onNext(locationId)

                            et_doc_date.setText(Sdf.formatDisplayFromSave(docDate))
                            slaughter.docDate = docDate

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
                    companySubject.onNext(CompanyOption(it.id!!, false))
                }.addTo(compositeDisposable)
    }

    private fun showLocationDialog(companyId: Long) {
        LocationDialogFragment
                .getInstance(fragmentManager!!, companyId)
                .selectEvent
                .subscribe {
                    locationSubject.onNext(it.id!!)
                }.addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun freezeEntry() {
        et_company.isEnabled = false
        et_location.isEnabled = false
        et_doc_date.isEnabled = false
        et_doc_no.isEnabled = false
        et_truck_code.isEnabled = false

        rb_doc_type_ift.isEnabled = false
        rb_doc_type_pl.isEnabled = false

        rb_type_a.isEnabled = false
        rb_type_b.isEnabled = false
        rb_type_kfc.isEnabled = false
    }

    private fun unfreezeEntry() {
        et_company.isEnabled = true
        et_location.isEnabled = true
        et_doc_date.isEnabled = true
        et_doc_no.isEnabled = true
        et_truck_code.isEnabled = true

        companySubject.onNext(CompanyOption(0, true))
        locationSubject.onNext(0)
        et_doc_no.text?.clear()
        et_truck_code.text?.clear()

        rb_doc_type_ift.isEnabled = true
        rb_doc_type_pl.isEnabled = true
        rb_doc_type_ift.isChecked = false
        rb_doc_type_pl.isChecked = false

        rb_type_a.isEnabled = true
        rb_type_b.isEnabled = true
        rb_type_kfc.isEnabled = true
        rb_type_a.isChecked = false
        rb_type_b.isChecked = false
        rb_type_kfc.isChecked = false
    }
}
