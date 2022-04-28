package my.com.engpeng.epslaughterhouse.fragment.receive


import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rece_history_detail.*
import kotlinx.android.synthetic.main.list_item_rece_detail.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.HistoryMortalityDialogFragment
import my.com.engpeng.epslaughterhouse.model.ShReceiveDetail
import my.com.engpeng.epslaughterhouse.di.PrintModule
import my.com.engpeng.epslaughterhouse.model.PrintData
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject

class ReceHistoryDetailFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val printModule: PrintModule by inject()

    private var receId: Long = 0
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_rece_history_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_detail, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_print -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val qrText = printModule.constructReceiveQrText(receId)
                    val printText = printModule.constructReceivePrintout(receId)

                    withContext(Dispatchers.Main) {
                        findNavController().navigate(
                            ReceHistoryDetailFragmentDirections.actionReceHistoryDetailFragmentToPrintPreviewFragment(
                                PrintData(
                                    qrText,
                                    printText
                                )
                            )
                        )
                    }
                }
                true
            }
            R.id.mi_delete -> {
                delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        receId = ReceHistoryDetailFragmentArgs.fromBundle(arguments!!).shReceiveId

        CoroutineScope(Dispatchers.IO).launch {
            val rece = appDb.shReceiveDao().getDpById(receId)
            withContext(Dispatchers.Main) {
                rece.run {
                    et_company.setText(companyName)
                    et_location.setText(locationName)
                    et_doc_date.setText(docDate)
                    et_doc_no.setText("${docType}-${docNo}")
                    et_type.setText(type)
                    et_truck_code.setText(truckCode)
                    if (isUpload == 0 && isDelete == 0) {
                        menu?.findItem(R.id.mi_delete)?.isVisible = true
                    }
                }
            }

            val detail = appDb.shReceiveDetailDao().getAllByShReceiveId(receId)
            withContext(Dispatchers.Main) {
                if (detail.isNotEmpty()) {
                    rv.layoutManager = LinearLayoutManager(context)
                    rv.adapter = DetailAdapter(detail)
                }
            }

            val receTtl = appDb.shReceiveDetailDao().getTtlByShReceiveId(receId)
            withContext(Dispatchers.Main) {
                tv_ttl_weight.text = receTtl.ttlWeight.format2Decimal()
                tv_ttl_cage.text = receTtl.ttlCage.toString()
            }
        }

        btn_mortality.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val mortalityList = appDb.shReceiveMortalityDao().getAllByShReceiveId(receId)
                withContext(Dispatchers.Main) {
                    HistoryMortalityDialogFragment.show(fragmentManager!!, mortalityList)
                }
            }
        }
    }

    private fun delete() {
        ConfirmDialogFragment.show(fragmentManager!!,
            getString(R.string.dialog_title_delete_receive),
            getString(R.string.dialog_confirm_msg_delete_receive),
            getString(R.string.delete), object : ConfirmDialogFragment.Listener {
                override fun onPositiveButtonClicked() {

                    CoroutineScope(Dispatchers.IO).launch {
                        val rece = appDb.shReceiveDao().getById(receId)
                        appDb.shReceiveDao().insert(rece.apply { isDelete = 1 })
                        withContext(Dispatchers.Main) {
                            AlertDialogFragment.show(
                                fragmentManager!!,
                                getString(R.string.success),
                                getString(R.string.dialog_success_delete)
                            )
                        }
                    }
                }

                override fun onNegativeButtonClicked() {}
            })
    }
}

class DetailAdapter(private val detailList: List<ShReceiveDetail>) :
    RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_rece_detail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        detailList[position].let { detail ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_house_code.text = detail.houseNo.toString()
                li_tv_weight.text = detail.weight.format2Decimal()
                li_tv_cage.text = detail.cage.toString()

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}