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
import kotlinx.android.synthetic.main.fragment_trip_conf.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempSlaughterMortalityAdapter
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.EnterMortalityDialogFragment
import my.com.engpeng.epslaughterhouse.model.TempTripMortality
import my.com.engpeng.epslaughterhouse.model.Trip
import my.com.engpeng.epslaughterhouse.model.TripDetail
import my.com.engpeng.epslaughterhouse.model.TripMortality
import my.com.engpeng.epslaughterhouse.util.PrintModule
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject


class TripConfFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val printModule: PrintModule by inject()
    private lateinit var trip: Trip
    private var rvAdapter = TempSlaughterMortalityAdapter()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_trip_conf, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRv()
    }

    private fun setupView() {
        trip = TripConfFragmentArgs.fromBundle(arguments!!).trip!!

        trip.run {
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

        appDb.tempTripDetailDao().getLiveTotal().observe(this,
                Observer {
                    et_ttl_weight.setText(it.ttlWeight.format2Decimal() + "Kg")
                    et_ttl_cage.setText(it.ttlCage.toString())
                })

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
                            appDb.tempTripMortalityDao().deleteById(tempId)
                        }
                    }

                    override fun onNegativeButtonClicked() {
                        rvAdapter.refresh()
                    }
                })
            }
        }).attachToRecyclerView(rv)

        fab_add.setOnClickListener {
            showMortalityDialog()
        }

        btn_save.setOnClickListener {
            ConfirmDialogFragment.show(fragmentManager!!,
                    "Confirm this trip ?",
                    "Edit is not allowed after save",
                    "Confirm", object : ConfirmDialogFragment.Listener {
                override fun onPositiveButtonClicked() {
                    save()
                }

                override fun onNegativeButtonClicked() {}
            })
        }
    }

    private fun setupRv() {
        appDb.tempTripMortalityDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })

        appDb.tempTripMortalityDao().getLiveTotal().observe(this,
                Observer {
                    tv_ttl_weight.text = it.ttlWeight.format2Decimal()
                    tv_ttl_qty.text = it.ttlQty.toString()
                })
    }

    private fun showMortalityDialog() {
        EnterMortalityDialogFragment
                .show(fragmentManager!!, object : EnterMortalityDialogFragment.Listener {
                    override fun onSubmit(tempTripMortality: TempTripMortality) {
                        CoroutineScope(Dispatchers.IO).launch {
                            appDb.tempTripMortalityDao().insert(tempTripMortality)
                        }
                    }
                })
    }

    private fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val tripId = appDb.tripDao().insert(trip)

                val tempDetailList = appDb.tempTripDetailDao().getAll()
                val detailList = TripDetail.transformFromTempWithTripId(tripId, tempDetailList)
                appDb.tripDetailDao().insert(detailList)

                val tempMortalityList = appDb.tempTripMortalityDao().getAll()
                val mortalityList = TripMortality.transformFromTempWithTripId(tripId, tempMortalityList)
                appDb.tripMortalityDao().insert(mortalityList)

                appDb.tempTripDetailDao().deleteAll()
                appDb.tempTripMortalityDao().deleteAll()

                val printText = printModule.constructTripPrintout(tripId)

                withContext(Dispatchers.Main) {
                    findNavController().navigate(TripConfFragmentDirections.actionTripConfFragmentToPrintPreviewFragment(printText))
                }

            } catch (e: Exception) {
                AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), getString(R.string.error_desc, e.message))
            }
        }
    }
}
