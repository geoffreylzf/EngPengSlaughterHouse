package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rece_sum.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempShReceiveDetailAdapter
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.model.ShReceive
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject

class ReceSumFragment : Fragment() {

    private val appDb: AppDb by inject()

    private lateinit var shReceive: ShReceive
    private var rvAdapter = TempShReceiveDetailAdapter(false)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rece_sum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRv()
    }

    private fun setupView() {
        shReceive = ReceSumFragmentArgs.fromBundle(arguments!!).shReceive!!

        shReceive.run {
            et_doc_date.setText(Sdf.formatDisplayFromSave(docDate!!))
            et_doc_no.setText("${docType}-${docNo}")
            et_type.setText(type)
            et_truck_code.setText(truckCode)

            CoroutineScope(Dispatchers.IO).launch {
                val company = appDb.companyDao().getById(companyId!!)
                val location = appDb.locationDao().getById(locationId!!)

                withContext(Dispatchers.Main) {
                    et_company.setText(company.companyName)
                    et_location.setText(location.locationName)
                }
            }
        }

        rv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
            addOnScrollListener(
                    object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            if (dy > 0) {
                                fab_add.hide()
                            } else {
                                fab_add.show()
                            }
                            super.onScrolled(recyclerView, dx, dy)
                        }
                    }
            )
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val (tempId, weight) = viewHolder.itemView.tag as Pair<Long, Double>

                ConfirmDialogFragment.show(fragmentManager!!,
                        "Delete swiped data ?",
                        "Weight: ${weight.format2Decimal()}Kg",
                        "DELETE", object : ConfirmDialogFragment.Listener {
                    override fun onPositiveButtonClicked() {

                        CoroutineScope(Dispatchers.IO).launch {
                            appDb.tempShReceiveDetailDao().deleteById(tempId)
                        }

                    }

                    override fun onNegativeButtonClicked() {
                        rvAdapter.refresh()
                    }
                })
            }
        }).attachToRecyclerView(rv)

        fab_add.setOnClickListener{
            findNavController().navigate(ReceSumFragmentDirections.actionReceSumFragmentToReceDetailFragment(shReceive.houseStr))
        }

        btn_next.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val count = appDb.tempShReceiveDetailDao().getCount()
                withContext(Dispatchers.Main) {
                    if (count != 0) {
                        findNavController().navigate(ReceSumFragmentDirections.actionReceSumFragmentToReceConfFragment(shReceive))
                    } else {
                        AlertDialogFragment.show(fragmentManager!!,
                                getString(R.string.dialog_title_error),
                                getString(R.string.dialog_error_msg_no_detail))
                    }
                }
            }
        }
    }

    private fun setupRv() {
        appDb.tempShReceiveDetailDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })

        appDb.tempShReceiveDetailDao().getLiveTotal().observe(this,
                Observer {
                    tv_ttl_weight.text = it.ttlWeight.format2Decimal()
                    tv_ttl_cage.text = it.ttlCage.toString()
                })
    }
}
