package my.com.engpeng.epslaughterhouse.fragment.trip


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_head.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.camera.CameraPermission
import my.com.engpeng.epslaughterhouse.camera.ScanActivity
import my.com.engpeng.epslaughterhouse.camera.ScanBus
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.CompanyDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.DatePickerDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.LocationDialogFragment
import my.com.engpeng.epslaughterhouse.model.CompanyQr
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 *
 */
class TripHeadFragment : Fragment() {

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    private lateinit var calendarDocDate: Calendar

    private var slaughter = Slaughter()

    private val companySubject = PublishSubject.create<CompanyQr>()
    private val locationSubject = PublishSubject.create<Long>()

    private var comDlgDis: Disposable? = null
    private var locDlgDis: Disposable? = null

    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        calendarDocDate = Calendar.getInstance()
        return inflater.inflate(R.layout.fragment_trip_head, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        setupObservable()
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
            if(CameraPermission.check(requireActivity())){
                startActivity(Intent(context, ScanActivity::class.java))
            }else{
                CameraPermission.request(requireActivity())
            }
        }
    }

    private fun setupObservable() {

        companySubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (!it.isQr) {
                        slaughter.locationId = null
                        et_location.text?.clear()
                        showLocationDialog(it.id)
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap { appDb.companyDao().getById(it.id) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.companyId = id
                        et_company.setText(companyName)
                    }
                }.addTo(compositeDisposable)

        locationSubject.subscribeOn(Schedulers.io())
                .flatMap { appDb.locationDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.locationId = id
                        et_location.setText(locationName)
                    }
                }.addTo(compositeDisposable)

        ScanBus.scanSubject
                .subscribeOn(Schedulers.io())
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { scanText ->
                    if (scanText.isNotEmpty()) {
                        val arr = scanText.split("|")
                        if (arr.size == 7) {

                            val companyId = arr[0].toLong()
                            val locationId = arr[1].toLong()
                            val docDate = arr[2]
                            val docNo = arr[3]
                            val docType = arr[4]
                            val type = arr[5]
                            val truckCode = arr[6]

                            companySubject.onNext(CompanyQr(companyId, true))
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
                        } else {
                            AlertDialogFragment.show(fragmentManager!!, getString(R.string.error_dialog_title), "Invalid QR code")
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
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.error_dialog_title), message)
            return
        }

        activity?.hideKeyboard()
        findNavController().navigate(TripHeadFragmentDirections.actionTripHeadFragmentToTripSumFragment(slaughter))
    }

    private fun showCompanyDialog() {
        comDlgDis = CompanyDialogFragment
                .getInstance(fragmentManager!!)
                .selectEvent
                .subscribe {
                    companySubject.onNext(CompanyQr(it.id!!, false))
                }
    }

    private fun showLocationDialog(companyId: Long) {
        locDlgDis = LocationDialogFragment
                .getInstance(fragmentManager!!, companyId)
                .selectEvent
                .subscribe {
                    locationSubject.onNext(it.id!!)
                }
    }

    override fun onPause() {
        super.onPause()
        //comDlgDis?.dispose() DO NOT DISPOSE BECAUSE IT WILL COMPLETE
        //locDlgDis?.dispose() DO NOT DISPOSE BECAUSE IT WILL COMPLETE
        compositeDisposable.clear()
    }
}
