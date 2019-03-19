package my.com.engpeng.epslaughterhouse.fragment.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
import my.com.engpeng.epslaughterhouse.util.RC_GOOGLE_SIGN_IN
import my.com.engpeng.epslaughterhouse.util.UiUtils
import my.com.engpeng.epslaughterhouse.util.appVersion
import my.com.engpeng.epslaughterhouse.util.toast
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var dlProgress: Dialog
    private var email: String? = null

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
        setupGoogleSignIn()

        btn_login.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()

            when {
                email == null || (email?.isEmpty()
                        ?: true) -> AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.dialog_error_msg_login_email))
                username.isEmpty() -> AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.dialog_error_msg_no_username))
                password.isEmpty() -> AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.dialog_error_msg_no_password))
                else -> {

                    val user = User(username, password)
                    dlProgress.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val apiResponse = apiModule.provideApiService(cb_local.isChecked).loginAsync(user.credentials, email).await()
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

    override fun onStart() {
        super.onStart()
        populateUi(GoogleSignIn.getLastSignedInAccount(context!!))
    }

    override fun onDetach() {
        super.onDetach()
        dlProgress.dismiss()
    }

    private fun setupGoogleSignIn() {
        googleSignInClient = GoogleSignIn.getClient(context!!, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build())

        btn_sign_in_gmail.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(context!!)
            if (account != null) {
                googleSignInClient.signOut()
                populateUi(GoogleSignIn.getLastSignedInAccount(context!!))
                context!!.toast("Sign out from google account")
            } else {
                val signInIntent = googleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
            }
        }
    }

    private fun populateUi(account: GoogleSignInAccount?) {
        if (account != null) {
            email = account.email
            setGoogleSignInButtonText(account.email!! + " (Sign out)")
        } else {
            email = null
            setGoogleSignInButtonText("Sign in")
        }
    }

    private fun setGoogleSignInButtonText(buttonText: String) {
        for (i in 0 until btn_sign_in_gmail.childCount) {
            val v = btn_sign_in_gmail.getChildAt(i)

            if (v is TextView) {
                v.text = buttonText
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                populateUi(account)
            } catch (e: ApiException) {
                if (e.statusCode != 12501) {
                    context!!.toast(getString(R.string.error_desc, e.toString()))
                }
                populateUi(null)
            }
        }
    }
}