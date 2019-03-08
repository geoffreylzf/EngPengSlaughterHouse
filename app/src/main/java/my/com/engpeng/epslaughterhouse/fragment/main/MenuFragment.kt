package my.com.engpeng.epslaughterhouse.fragment.main


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.merge_menu_log.*
import kotlinx.android.synthetic.main.merge_menu_trip.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.service.UploadService
import my.com.engpeng.epslaughterhouse.util.I_KEY_LOCAL
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.appVersion
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupView() {
        tv_version.text = context?.appVersion()
        appDb.slaughterDao().getLiveCountByUpload(0).observe(this, Observer {
            tv_upload_count.text = it.toString()
        })

        appDb.logDao().getLiveLastLogByTask(LOG_TASK_UPLOAD).observe(this, Observer {
            it?.let { log ->
                tv_upload_dt.text = log.datetime
                tv_upload_msg.text = log.remark
            }
        })

        appDb.slaughterDao().getLiveCountByDate(Sdf.getCurrentDate()).observe(this, Observer {
            tv_trip_confirm.text = it.confirmCount.toString()
            tv_trip_delete.text = it.deleteCount.toString()
        })
    }

    private fun setupListener() {
        btn_trip.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToTripHeadFragment())
        }

        btn_upload.setOnClickListener {
            appDb.slaughterDao().getCountByUpload(0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 0) {
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.error),
                                    getString(R.string.dialog_error_msg_no_data_upload))
                        } else {
                            val intent = Intent(activity, UploadService::class.java).apply {
                                putExtra(I_KEY_LOCAL, cb_local.isChecked)
                            }
                            activity!!.stopService(intent)
                            activity!!.startService(intent)
                        }
                    }, {}).addTo(compositeDisposable)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}
