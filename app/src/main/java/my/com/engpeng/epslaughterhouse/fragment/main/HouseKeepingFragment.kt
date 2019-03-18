package my.com.engpeng.epslaughterhouse.fragment.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_house_keeping.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()

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

    private fun retrieveHouseKeeping() {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = apiModule.provideApiService(cb_local.isChecked)
                val companyList = api.getCompanyListAsync().await().result

                appDb.companyDao().deleteAllAsync()
                appDb.companyDao().insertAsync(companyList)
                appDb.tableLogDao().insertAsync(TableLog(Company.TABLE_NAME, Sdf.getCurrentDateTime(), companyList.size, companyList.size))


                val locationList = api.getLocationListAsync().await().result
                appDb.locationDao().deleteAllAsync()
                appDb.locationDao().insertAsync(locationList)
                appDb.tableLogDao().insertAsync(TableLog(Location.TABLE_NAME, Sdf.getCurrentDateTime(), locationList.size, locationList.size))

                withContext(Dispatchers.Main) {
                    AlertDialogFragment.show(fragmentManager!!,
                            getString(R.string.dialog_title_success),
                            getString(R.string.dialog_success_msg_retrieve))

                }
            } catch (e: Exception) {
                AlertDialogFragment.show(fragmentManager!!,
                        getString(R.string.dialog_title_error),
                        getString(R.string.error_desc, e.message))
            }

        }
    }
}
