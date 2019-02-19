package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.util.*

class DatePickerDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun show(fm: FragmentManager, calendar: Calendar, listener: DatePickerDialog.OnDateSetListener) {
            DatePickerDialogFragment().apply {
                this.calendar = calendar
                this.listener = listener
            }.show(fm, TAG)
        }
    }

    private lateinit var calendar: Calendar
    private lateinit var listener: DatePickerDialog.OnDateSetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), listener, year, month, day)
    }
}