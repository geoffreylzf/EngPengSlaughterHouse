package my.com.engpeng.epslaughterhouse.fragment.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_house_keeping.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.BaseDao
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location

class HouseKeepingFragment : Fragment() {

    companion object {
        val TAG = HouseKeepingFragment::class.qualifiedName
    }

    private val appDb by lazy { AppModule.provideDb(requireContext()) }
    private val apiService by lazy { AppModule.provideApiService() }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        childFragmentManager
                .beginTransaction()
                .add(R.id.fl_company, HkTableInfoFragment<BaseDao<Company>, Company>().apply {
                    entity = Company()
                    dao = appDb.companyDao()
                    tableLogDao = appDb.tableLogDao()
                })
                .add(R.id.fl_location, HkTableInfoFragment<BaseDao<Location>, Location>().apply {
                    entity = Location()
                    dao = appDb.locationDao()
                    tableLogDao = appDb.tableLogDao()
                })
                .commit()

        return inflater.inflate(R.layout.fragment_house_keeping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_resync_all.setOnClickListener {
            retrieveHouseKeeping()
        }

    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun retrieveHouseKeeping() {
        apiService.getCompanyList()
                .subscribeOn(Schedulers.io())
                .doOnNext { response ->
                    appDb.companyDao().run {
                        deleteAll()
                        insert(response.result)
                    }
                }
                .flatMap {
                    apiService.getLocationList()
                }
                .doOnNext { response ->
                    appDb.locationDao().run {
                        deleteAll()
                        insert(response.result)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {},
                        { error ->
                            AlertDialogFragment.show(fragmentManager!!,
                                    "Error Retrieve House Keeping",
                                    "Error : " + error.message)
                        },
                        {
                            AlertDialogFragment.show(fragmentManager!!,
                                    "Retrieve House Keeping",
                                    "Retrieve Complete")
                        }
                ).addTo(compositeDisposable)
    }
}
