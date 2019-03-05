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
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.db.BaseDao
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Company
import my.com.engpeng.epslaughterhouse.model.Location
import my.com.engpeng.epslaughterhouse.model.TableLog
import my.com.engpeng.epslaughterhouse.util.Sdf
import org.koin.android.ext.android.inject

class HouseKeepingFragment : Fragment() {

    companion object {
        val TAG = HouseKeepingFragment::class.qualifiedName
    }

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()

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
        apiModule.provideApiService(cb_local.isChecked).getCompanyList()
                .subscribeOn(Schedulers.io())
                .doOnNext { response ->
                    if (response.isSuccess()) {
                        appDb.companyDao().run {
                            deleteAll()
                            insert(response.result)
                        }

                    } else {
                        throw Exception(getString(R.string.dialog_error_msg_retrieve_company))
                    }

                }
                .flatMapSingle {
                    appDb.tableLogDao().insert(TableLog(Company.TABLE_NAME, Sdf.getCurrentDateTime(), it.result.size, it.result.size))
                }
                .flatMap {
                    apiModule.provideApiService(cb_local.isChecked).getLocationList()
                }
                .doOnNext { response ->
                    if (response.isSuccess()) {
                        appDb.locationDao().run {
                            deleteAll()
                            insert(response.result)
                        }
                    } else {
                        throw Exception(getString(R.string.dialog_error_msg_retrieve_location))
                    }
                }
                .flatMapSingle {
                    appDb.tableLogDao().insert(TableLog(Location.TABLE_NAME, Sdf.getCurrentDateTime(), it.result.size, it.result.size))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {},
                        { error ->
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.dialog_title_error),
                                    getString(R.string.error_desc, error.message))
                        },
                        {
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.dialog_title_success),
                                    getString(R.string.dialog_success_msg_retrieve))
                        }
                ).addTo(compositeDisposable)
    }
}
