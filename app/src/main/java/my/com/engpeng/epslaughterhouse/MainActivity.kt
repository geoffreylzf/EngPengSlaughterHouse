package my.com.engpeng.epslaughterhouse

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.main.MenuFragmentDirections
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val appDb: AppDb by inject()
    private val sharedPreferencesModule: SharedPreferencesModule by inject()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_tb)

        appBarConfiguration = AppBarConfiguration(
                setOf(R.id.splashFragment, R.id.menuFragment),
                main_dl)

        val navController = this.findNavController(R.id.main_fm_navigation)

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, nd, _ ->
            if (nd.id == R.id.menuFragment) {
                main_dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                main_dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            if (nd.id == R.id.loginFragment) {
                main_tb.visibility = View.GONE
            } else {
                main_tb.visibility = View.VISIBLE
            }
        }
        main_nv_start?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mi_logout -> {
                    main_dl.closeDrawer(GravityCompat.START)
                    onPerformLogout()
                }
                R.id.mi_house_keeping -> {
                    findNavController(R.id.main_fm_navigation).navigate(MenuFragmentDirections.actionMenuFragmentToHouseKeepingFragment())
                }
                R.id.mi_upload -> {
                    findNavController(R.id.main_fm_navigation).navigate(MenuFragmentDirections.actionMenuFragmentToUploadFragment())
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_fm_navigation).navigateUp(appBarConfiguration)
    }

    private fun onPerformLogout() {

        CoroutineScope(Dispatchers.IO).launch {
            val count = appDb.utilDao().getUnuploadCount()
            withContext(Dispatchers.Main) {
                if (count > 0) {
                    AlertDialogFragment.show(supportFragmentManager,
                            getString(R.string.error),
                            getString(R.string.dialog_error_msg_got_un_upload))
                } else {
                    ConfirmDialogFragment.show(supportFragmentManager,
                            getString(R.string.dialog_title_logout),
                            getString(R.string.dialog_confirm_msg_logout),
                            getString(R.string.logout), object : ConfirmDialogFragment.Listener {
                        override fun onPositiveButtonClicked() {
                            sharedPreferencesModule.removeUser()
                            findNavController(R.id.main_fm_navigation).navigate(MenuFragmentDirections.actionMenuFragmentToLoginFragment())
                        }

                        override fun onNegativeButtonClicked() {}
                    })
                }
            }
        }
    }

    fun setNavHeader() {
        main_nv_start?.getHeaderView(0)?.run {
            findViewById<TextView>(R.id.tv_username)?.text = sharedPreferencesModule.getUser().username
            findViewById<TextView>(R.id.tv_unique_id)?.text = sharedPreferencesModule.getUniqueId()
        }
    }

    override fun onBackPressed() {
        if (main_dl.isDrawerOpen(GravityCompat.START)) {
            main_dl.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
