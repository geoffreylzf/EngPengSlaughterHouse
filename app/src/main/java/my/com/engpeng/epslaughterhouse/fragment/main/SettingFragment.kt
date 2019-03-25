package my.com.engpeng.epslaughterhouse.fragment.main


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setting.*

import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.DATABASE_NAME
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.util.RC_WRITE
import my.com.engpeng.epslaughterhouse.util.Sdf
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingFragment : Fragment() {

    private val spm: SharedPreferencesModule by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackup()
        setupLocalCheckBox(spm.getIsLocal())

    }

    private fun setupLocalCheckBox(local: Boolean) {
        cb_local.isChecked = local
        cb_local.setOnCheckedChangeListener { _, b ->
            spm.saveIsLocal(b)
        }
    }

    private fun setupBackup() {
        btn_backup.setOnClickListener {
            if (checkWritePermission()) {

                if (backupDatabase()) {
                    AlertDialogFragment.show(fragmentManager!!,
                            getString(R.string.success),
                            getString(R.string.dialog_success_backup))
                }

            } else run { requestWritePermission() }
        }
    }

    private fun checkWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RC_WRITE)
    }

    private fun backupDatabase(): Boolean {
        val packageName = activity!!.packageName
        val dbPath = "/data/data/$packageName/databases/$DATABASE_NAME"
        val backupPath = "/sdcard/${Sdf.getBackupDatetimeFormat()}_$DATABASE_NAME"

        try {

            val fis = FileInputStream(File(dbPath))
            val fos = FileOutputStream(File(backupPath))

            val buffer = ByteArray(1024)
            while (fis.read(buffer) > 0) {
                fos.write(buffer)
            }

            fos.flush()
            fos.close()
            fis.close()


        } catch (e: Exception) {
            AlertDialogFragment.show(fragmentManager!!,
                    getString(R.string.error),
                    getString(R.string.error_desc, e.message))
            return false
        }
        return true
    }
}
