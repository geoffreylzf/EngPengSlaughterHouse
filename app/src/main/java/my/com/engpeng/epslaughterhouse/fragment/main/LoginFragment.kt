package my.com.engpeng.epslaughterhouse.fragment.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.User
import my.com.engpeng.epslaughterhouse.util.UiUtils
import my.com.engpeng.epslaughterhouse.util.appVersion
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private lateinit var dlProgress: Dialog

    private val apiModule: ApiModule by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        dlProgress = UiUtils.getProgressDialog(context!!)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_version.text = context?.appVersion()

        btn_login.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()

            when {
                username.isEmpty() -> AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.dialog_error_msg_no_username))
                password.isEmpty() -> AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.dialog_error_msg_no_password))
                else -> {

                    val user = User(username, password)
                    dlProgress.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val apiResponse = apiModule.provideApiService(cb_local.isChecked).loginAsync(user.credentials).await()
                            withContext(Dispatchers.Main) {
                                dlProgress.hide()
                                if (apiResponse.isSuccess() && apiResponse.result.success) {
                                    sharedPreferencesModule.saveUser(user)
                                    sharedPreferencesModule.generateSaveUniqueId()
                                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMenuFragment())
                                } else {
                                    AlertDialogFragment.show(fragmentManager!!,
                                            getString(R.string.login_error_desc, apiResponse.cod.toString()),
                                            getString(R.string.error_desc, apiResponse.result.message))
                                }
                            }
                        } catch (e: Exception) {
                            AlertDialogFragment.show(fragmentManager!!,
                                    getString(R.string.error),
                                    getString(R.string.error_desc, e.message))
                        }
                    }
                }
            }
        }
        btn_cancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        dlProgress.dismiss()
    }
}
