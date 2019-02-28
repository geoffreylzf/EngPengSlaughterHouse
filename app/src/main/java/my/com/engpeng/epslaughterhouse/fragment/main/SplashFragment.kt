package my.com.engpeng.epslaughterhouse.fragment.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_splash.*

import my.com.engpeng.epslaughterhouse.R

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_menu.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_splashFragment_to_menuFragment))
        btn_login.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_splashFragment_to_loginFragment))
    }
}
