package my.com.engpeng.epslaughterhouse

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

        main_nv_start?.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_fm_navigation).navigateUp(appBarConfiguration)
    }
}
