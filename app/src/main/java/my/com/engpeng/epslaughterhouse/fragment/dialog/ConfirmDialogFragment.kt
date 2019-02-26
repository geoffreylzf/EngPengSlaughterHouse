package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import my.com.engpeng.epslaughterhouse.R

class ConfirmDialogFragment : DialogFragment() {

    companion object {
        private val TAG = this::class.qualifiedName
        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_MESSAGE = "KEY_MESSAGE"
        const val KEY_POSITIVE_TEXT = "KEY_POSITIVE_TEXT"

        fun show(fm: FragmentManager, title: String, message: String, positiveText: String, listener: Listener) {
            ConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_MESSAGE, message)
                    putString(KEY_POSITIVE_TEXT, positiveText)
                }
                this.listener = listener
            }.show(fm, TAG)
        }
    }

    lateinit var listener: Listener

    interface Listener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(arguments!!.getString(ConfirmDialogFragment.KEY_TITLE))
                .setMessage(arguments!!.getString(ConfirmDialogFragment.KEY_MESSAGE))
                .setPositiveButton(arguments!!.getString(ConfirmDialogFragment.KEY_POSITIVE_TEXT))
                { _, _ -> listener.onPositiveButtonClicked() }
                .setNegativeButton(R.string.dialog_negative_button_text)
                { _, _ -> listener.onNegativeButtonClicked() }
                .create()
    }
}