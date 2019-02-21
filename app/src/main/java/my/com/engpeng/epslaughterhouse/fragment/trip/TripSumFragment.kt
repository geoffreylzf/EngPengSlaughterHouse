package my.com.engpeng.epslaughterhouse.fragment.trip


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_trip_sum.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.adapter.TempSlaughterDetailAdapter
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.model.Slaughter
import my.com.engpeng.epslaughterhouse.util.Sdf
import my.com.engpeng.epslaughterhouse.util.format2Decimal

class TripSumFragment : Fragment() {

    private val appDb by lazy { AppModule.provideDb(requireContext()) }

    private lateinit var slaughter: Slaughter
    private var rvAdapter = TempSlaughterDetailAdapter(false)

    private val companySubject = PublishSubject.create<Long>()
    private var companyDis: Disposable? = null
    private val locationSubject = PublishSubject.create<Long>()
    private var locationDis: Disposable? = null
    private var delTempDis: Disposable? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_sum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservable()
        setupView()
        setupRv()
    }

    private fun setupView() {
        fab_add.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_tripSumFragment_to_tripDetailFragment))
        btn_next.setOnClickListener {
            findNavController().navigate(TripSumFragmentDirections.actionTripSumFragmentToTripConfFragment(slaughter))
        }
        slaughter = TripSumFragmentArgs.fromBundle(arguments!!).slaughter!!

        slaughter.run {
            companySubject.onNext(companyId!!)
            locationSubject.onNext(locationId!!)
            et_doc_date.setText(Sdf.formatDisplayFromSave(docDate!!))
            et_doc_no.setText(docNo)
            et_type.setText(type)
            et_truck_code.setText(truckCode)
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
                        delTempDis = Single
                                .fromCallable { appDb.tempSlaughterDetailDao().deleteById(tempId) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe()
                    }

                    override fun onNegativeButtonClicked() {
                        rvAdapter.refresh()
                    }
                })
            }
        }).attachToRecyclerView(rv)
    }

    private fun setupRv() {
        appDb.tempSlaughterDetailDao().getLiveAll().observe(this,
                Observer {
                    rvAdapter.setList(it)
                })

        appDb.tempSlaughterDetailDao().getLiveTotal().observe(this,
                Observer {
                    tv_ttl_weight.text = it.ttlWeight.format2Decimal()
                    tv_ttl_qty.text = it.ttlQty.toString()
                    tv_ttl_cage.text = it.ttlCage.toString()
                    tv_ttl_cover.text = it.ttlCover.toString()
                })
    }

    private fun setupObservable() {
        companyDis = companySubject.subscribeOn(Schedulers.io())
                .flatMap { appDb.companyDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.companyId = id
                        et_company.setText(companyName)
                    }
                }

        locationDis = locationSubject.subscribeOn(Schedulers.io())
                .flatMap { appDb.locationDao().getById(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.run {
                        slaughter.locationId = id
                        et_location.setText(locationName)
                    }
                }
    }

    override fun onStop() {
        super.onStop()
        companyDis?.dispose()
        locationDis?.dispose()
        delTempDis?.dispose()
    }
}
