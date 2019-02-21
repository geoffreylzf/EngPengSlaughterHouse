package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_detail.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempSlaughterDetailAdapter
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.TempSlaughterDetail
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import my.com.engpeng.epslaughterhouse.util.requestFocusWithKeyboard
import my.com.engpeng.epslaughterhouse.util.vibrate
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 *
 */
class TripDetailFragment : Fragment() {

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    private var tempSlaughterDetail = TempSlaughterDetail()
    private var rvAdapter = TempSlaughterDetailAdapter(true)

    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRv()
    }

    private fun setupView() {
        rb_cage_1.setOnClickListener { setupSpinnerCoverQty(1, 1) }
        rb_cage_2.setOnClickListener { setupSpinnerCoverQty(2, 2) }
        rb_cage_3.setOnClickListener { setupSpinnerCoverQty(3, 3) }
        rb_cage_4.setOnClickListener { setupSpinnerCoverQty(4, 4) }
        rb_cage_5.setOnClickListener { setupSpinnerCoverQty(5, 5) }
        rb_cage_6.setOnClickListener { setupSpinnerCoverQty(6, 6) }

        setupSpinnerCoverQty(0, 0)

        et_weight.requestFocusWithKeyboard(requireActivity())

        btn_save.setOnClickListener { save() }

        btn_cancel.setOnClickListener {
            backToSummary()
        }

        rv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
    }

    private fun setupRv() {
        appDb.tempSlaughterDetailDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })
    }

    private fun setupSpinnerCoverQty(qty: Int, default: Int?) {
        sn_cover.run {
            adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, ArrayList<Int>().apply {
                for (i in 0..qty) {
                    add(i)
                }
            }).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            default?.run {
                setSelection(this)
            }
        }
    }

    private fun save() {
        tempSlaughterDetail.run {
            weight = et_weight.text.toString().toDoubleOrNull()
            qty = et_qty.text.toString().toIntOrNull()

            cage = when (rg_cage.checkedRadioButtonId) {
                R.id.rb_cage_1 -> 1
                R.id.rb_cage_2 -> 2
                R.id.rb_cage_3 -> 3
                R.id.rb_cage_4 -> 4
                R.id.rb_cage_5 -> 5
                R.id.rb_cage_6 -> 6
                else -> 0
            }
            cover = sn_cover.selectedItem.toString().toIntOrNull()
        }

        var message = ""
        tempSlaughterDetail.run check@{
            if (weight == null) {
                message = "Please enter weight"
                return@check
            }
            if (qty == null || qty == 0) {
                message = "Please enter quantity"
                return@check
            }
            if (cage == null || cage == 0) {
                message = "Please select cage"
                return@check
            }
            if (cover == null) {
                message = "Please select cover"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.error_dialog_title), message)
            return
        }

        disposable = appDb.tempSlaughterDetailDao()
                .insert(tempSlaughterDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    activity?.vibrate()
                    et_weight.text?.clear()
                    et_weight.requestFocusWithKeyboard(activity)
                }, {})
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
        disposable2?.dispose()
    }

    private fun backToSummary() {
        activity?.hideKeyboard()
        disposable2 = Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    findNavController().popBackStack()
                }
    }
}
