package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import my.com.engpeng.epslaughterhouse.R

class AlertDialogFragment : DialogFragment() {

    companion object {
        private val TAG = this::class.qualifiedName
        const val B_KEY_TITLE = "B_KEY_TITLE"
        const val B_KEY_MESSAGE = "B_KEY_MESSAGE"

        fun show(fm: FragmentManager, title: String, message: String) {
            AlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(B_KEY_TITLE, title)
                    putString(B_KEY_MESSAGE, message)
                }
            }.show(fm, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(arguments!!.getString(B_KEY_TITLE))
                .setMessage(arguments!!.getString(B_KEY_MESSAGE))
                .setPositiveButton(R.string.dialog_positive_button_text) { _, _ -> dismiss() }
                .create()
    }
}