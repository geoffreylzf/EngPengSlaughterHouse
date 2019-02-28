package my.com.engpeng.epslaughterhouse.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.AppModule
import okhttp3.Credentials

class LoginFragment : Fragment() {

    private val apiService by lazy { AppModule.provideApiService() }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login.setOnClickListener {

            val credentials = Credentials.basic("geoffrey.lee", "12345")

            apiService.login(credentials, "geoffreylzf@gmail.com")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.e("Login", it.toString())
                    }.addTo(compositeDisposable)
            //findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}
