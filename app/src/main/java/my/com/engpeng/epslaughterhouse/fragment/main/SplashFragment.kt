package my.com.engpeng.epslaughterhouse.fragment.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sharedPreferencesModule.getUser().isValid()) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMenuFragment())
        } else {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }
    }


}
