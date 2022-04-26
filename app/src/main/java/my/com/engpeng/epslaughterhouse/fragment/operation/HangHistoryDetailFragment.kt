package my.com.engpeng.epslaughterhouse.fragment.operation


import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_hang_history_detail.*
import kotlinx.android.synthetic.main.list_item_hang_mortality.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.fragment.dialog.ConfirmDialogFragment
import my.com.engpeng.epslaughterhouse.model.ShHangMortality
import my.com.engpeng.epslaughterhouse.di.PrintModule
import my.com.engpeng.epslaughterhouse.util.format2Decimal
import org.koin.android.ext.android.inject

class HangHistoryDetailFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val printModule: PrintModule by inject()

    private var hangId: Long = 0
    private var menu: Menu? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_hang_history_detail, container, false)
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
                    val printText = printModule.constructHangPrintout(hangId)
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(HangHistoryDetailFragmentDirections.actionHangHistoryDetailFragmentToPrintPreviewFragment(printText))
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
        hangId = HangHistoryDetailFragmentArgs.fromBundle(arguments!!).hangId

        CoroutineScope(Dispatchers.IO).launch {
            val hang = appDb.shHangDao().getById(hangId)

            withContext(Dispatchers.Main) {
                hang.run {
                    et_doc_no.setText(docNo)
                    et_remark.setText(if (remark.isNullOrEmpty()) " " else remark)
                    tv_doc_id.text = docId.toString()
                    if (isUpload == 0 && isDelete == 0) {
                        menu?.findItem(R.id.mi_delete)?.isVisible = true
                    }
                }
            }

            val morList = appDb.shHangMortalityDao().getAllByShHangId(hangId)
            withContext(Dispatchers.Main) {
                if (morList.isNotEmpty()) {
                    rv.layoutManager = LinearLayoutManager(context)
                    rv.adapter = MorAdapter(morList)
                }
            }

            val tripTtl = appDb.shHangMortalityDao().getTtlByShHangId(hangId)
            withContext(Dispatchers.Main) {
                tv_ttl_weight.text = tripTtl.ttlWeight.format2Decimal()
                tv_ttl_qty.text = tripTtl.ttlQty.toString()
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
                    val hang = appDb.shHangDao().getById(hangId)
                    appDb.shHangDao().insert(hang.apply { isDelete = 1 })
                    withContext(Dispatchers.Main) {
                        AlertDialogFragment.show(fragmentManager!!, getString(R.string.success), getString(R.string.dialog_success_delete))
                    }
                }
            }

            override fun onNegativeButtonClicked() {}
        })
    }
}

class MorAdapter(private val morList: List<ShHangMortality>)
    : RecyclerView.Adapter<MorAdapter.MorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MorViewHolder {
        return MorViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_hang_mortality, parent, false))
    }

    override fun getItemCount(): Int {
        return morList.size
    }

    override fun onBindViewHolder(holder: MorViewHolder, position: Int) {
        morList[position].let { mor ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = mor.weight.format2Decimal()
                li_tv_qty.text = mor.qty.toString()

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    class MorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}