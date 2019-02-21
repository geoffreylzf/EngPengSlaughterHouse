package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_mortality.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.TempSlaughterMortality
import my.com.engpeng.epslaughterhouse.util.requestFocusWithKeyboard
import my.com.engpeng.epslaughterhouse.util.vibrate

class MortalityDialogFragment : DialogFragment() {
    companion object {
        val TAG = this::class.qualifiedName
        fun getInstance(fm: FragmentManager): MortalityDialogFragment {
            return MortalityDialogFragment().apply {
                show(fm, TAG)
            }
        }
    }

    private val doneSubject = PublishSubject.create<TempSlaughterMortality>()
    private val temp = TempSlaughterMortality()

    val doneEvent: Observable<TempSlaughterMortality> = doneSubject

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_mortality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_save.setOnClickListener {
            temp.run{
                weight = et_weight.text.toString().toDoubleOrNull()
                qty = et_qty.text.toString().toIntOrNull()
            }

            var message = ""
            temp.run check@{
                if(weight == null){
                    message = "Please enter weight"
                    return@check
                }
                if (qty == null || qty == 0) {
                    message = "Please enter quantity"
                    return@check
                }
            }

            if (message.isNotEmpty()) {
                AlertDialogFragment.show(fragmentManager!!, getString(R.string.error_dialog_title), message)
            }else{
                doneSubject.onNext(temp)
                activity?.vibrate()
                dismiss()
            }
        }
        btn_cancel.setOnClickListener {
            dismiss()
        }
        et_weight.requestFocusWithKeyboard(activity)

    }
}