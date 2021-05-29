package my.com.engpeng.epslaughterhouse.fragment

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_update_app_ver.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.util.UiUtils
import org.koin.android.ext.android.inject
import java.io.UnsupportedEncodingException
import java.util.*

class UpdateAppVerFragment : Fragment() {
    private var versionCode = 0
    private var versionName = ""
    private var appCode = ""

    private lateinit var dlProgress: Dialog
    private val apiModule: ApiModule by inject()

    val GLOBAL_HOST = "http://epgroup.dyndns.org:5031/"
    val LOCAL_HOST = "http://192.168.8.6/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        dlProgress = UiUtils.getProgressDialog(context!!)

        return inflater.inflate(R.layout.fragment_update_app_ver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setupView()
        this.setupListener()
    }

    private fun setupView() {
        val manager = this.activity?.packageManager
        if (manager != null) {
            val info = manager.getPackageInfo(this.activity?.packageName, PackageManager.GET_ACTIVITIES)
            if (info != null) {
                versionCode = info.versionCode
                versionName = info.versionName
                appCode = info.packageName

                tv_current_ver_code.text = "Current Version Name : " + versionName
                tv_current_ver_name.text = "Current Version Code : " + versionCode
            }
        }

    }

    private fun setupListener() {
        btn_update_app_ver.setOnClickListener {
            var host: String = GLOBAL_HOST
            if (cb_local.isChecked) {
                host = LOCAL_HOST
            }

            val url = String.format("%s%s%s%s",
                    host,
                    "api/info/mobile/apps/",
                    appCode,
                    "/latest")

            val queue = Volley.newRequestQueue(this.activity)

            val jsonRequest = JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    { response ->
                        dlProgress.hide()
                        try {
                            val latestVerCode = response.getInt("version_code")
                            when {
                                latestVerCode > versionCode -> {
                                    val uri = Uri.parse(response.getString("download_link"))
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                }
                                latestVerCode == versionCode -> {
                                    AlertDialogFragment.show(fragmentManager!!,
                                            getString(R.string.info),
                                            "Current app is the latest version")
                                }
                                else -> {
                                    AlertDialogFragment.show(fragmentManager!!,
                                            getString(R.string.info),
                                            String.format(Locale.ENGLISH, "Current Ver : %d \nLatest Ver : %d", versionCode, latestVerCode))
                                }
                            }
                        } catch (e: Exception) {
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.info),
                                    getString(R.string.error_desc, e.message))
                        }
                    }
            ) { error ->
                dlProgress.hide()
                var err = "Unknown error"
                if (error is TimeoutError) {
                    err = "Timeout error"
                } else {
                    if (error.networkResponse.data != null) {
                        try {
                            err = String(error.networkResponse.data, Charsets.UTF_8)
                        } catch (e: UnsupportedEncodingException) {
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.info),
                                    getString(R.string.error_desc, e.message))
                        }
                    }
                }
                AlertDialogFragment.show(fragmentManager!!,
                        getString(R.string.info),
                        getString(R.string.error_desc, err))
            }

            dlProgress.show()
            queue.add(jsonRequest)

        }
    }

}