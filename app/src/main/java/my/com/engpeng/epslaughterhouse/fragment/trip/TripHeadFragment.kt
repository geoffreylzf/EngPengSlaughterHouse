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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_head.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.activity.ScanActivity
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.CompanyDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.DatePickerDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.LocationDialogFragment
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TripHeadFragment : Fragment() {

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    private lateinit var calendarDocDate: Calendar
    private var isScanning: Boolean = false

    private var slaughter = Slaughter()

    private val companySubject = PublishSubject.create<Long>()
    private var companyDis: Disposable? = null

    private val locationSubject = PublishSubject.create<Long>()
    private var locationDis: Disposable? = null

    private var comDlgDis: Disposable? = null
    private var locDlgDis: Disposable? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        calendarDocDate = Calendar.getInstance()
        return inflater.inflate(R.layout.fragment_trip_head, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservable()
        setupView()
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
            startActivity(Intent(context, ScanActivity::class.java))
        }
    }

    private fun setupObservable() {
        companyDis = companySubject.subscribeOn(Schedulers.io())
                .flatMap { appDb.companyDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.companyId = id
                        et_company.setText(companyName)

                        if (!isScanning) {
                            slaughter.locationId = null
                            et_location.text?.clear()
                            showLocationDialog(id!!)
                        }
                    }
                }

        locationDis = locationSubject.subscribeOn(Schedulers.io())
                .flatMap { appDb.locationDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.locationId = id
                        et_location.setText(locationName)
                    }
                }
    }

    private fun start() {
        slaughter.run {
            docNo = et_doc_no.text.toString()
            truckCode = et_truck_code.text.toString()

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
                    companySubject.onNext(it.id!!)
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

    override fun onStop() {
        super.onStop()
        comDlgDis?.dispose()
        locDlgDis?.dispose()
        companyDis?.dispose()
        locationDis?.dispose()
    }


}
