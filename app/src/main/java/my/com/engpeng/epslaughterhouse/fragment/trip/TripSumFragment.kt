package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_sum.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.util.Sdf

class TripSumFragment : Fragment() {

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    lateinit var slaughter: Slaughter

    private val companySubject = PublishSubject.create<Long>()
    private var companyDis: Disposable? = null
    private val locationSubject = PublishSubject.create<Long>()
    private var locationDis: Disposable? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_sum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservable()
        setupView()
    }

    private fun setupView() {
        fab_add.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_tripSumFragment_to_tripDetailFragment))
        btn_next.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_tripSumFragment_to_tripConfFragment))
        slaughter = TripSumFragmentArgs.fromBundle(arguments!!).slaughter!!

        slaughter.run {
            companySubject.onNext(companyId!!)
            locationSubject.onNext(locationId!!)
            et_doc_date.setText(Sdf.formatDisplayFromSave(docDate!!))
            et_doc_no.setText(docNo)
            et_type.setText(type)
            et_truck_code.setText(truckCode)
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

    override fun onStop() {
        super.onStop()
        companyDis?.dispose()
        locationDis?.dispose()
    }
}
