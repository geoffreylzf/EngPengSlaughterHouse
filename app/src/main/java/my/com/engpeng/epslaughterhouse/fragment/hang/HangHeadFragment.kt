package my.com.engpeng.epslaughterhouse.fragment.hang


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_hang_head.*
import kotlinx.android.synthetic.main.list_item_doc_l.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Doc
import my.com.engpeng.epslaughterhouse.model.ShHang
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class HangHeadFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val vm: HangHeadViewModel by viewModel()
    private var hang = ShHang()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_hang_head, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.hang_head, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_history -> {
                findNavController().navigate(HangHeadFragmentDirections.actionHangHeadFragmentToHangHistoryFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        setupListener()
        vm.liveIsSelected.observe(this, androidx.lifecycle.Observer {
            freezeEntry(!it) //if true,
        })
    }

    private fun setupRv() {
        CoroutineScope(Dispatchers.IO).launch {
            val docList = appDb.docDao().getAll()
            withContext(Dispatchers.Main) {
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = DocAdapter(docList, object : DocAdapter.Listener {
                    override fun onClicked(doc: Doc) {
                        et_doc_no.setText(doc.docNo!!)
                        et_loc_name.setText(doc.locationName)
                        tv_doc_id.text = doc.id.toString()
                        vm.setIsSelected(true)
                        et_remark.requestFocus()
                    }
                })
            }
        }
    }

    private fun setupListener() {
        fab_refresh.setOnClickListener {
            et_doc_no.setText("")
            et_loc_name.setText("")
            et_remark.setText("")
            tv_doc_id.text = ""
            vm.setIsSelected(false)
            et_doc_no.requestFocus()
        }

        btn_start.setOnClickListener {
            start()
        }
    }

    private fun start(){
        hang.run {
            docId = tv_doc_id.text.toString().toLongOrNull()
            docNo = et_doc_no.text.toString()
            remark = et_remark.text.toString()
        }

        var message = ""
        hang.run check@{
            if (docNo == null || docNo!!.isEmpty()) {
                message = "Please enter document number"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(fragmentManager!!, getString(R.string.dialog_title_error), message)
            return
        }

        activity?.hideKeyboard()
        findNavController().navigate(HangHeadFragmentDirections.actionHangHeadFragmentToHangSumFragment(hang))
    }

    private fun freezeEntry(b: Boolean) {
        et_doc_no.isEnabled = b
        til_loc_name.visibility = if (b) View.GONE else View.VISIBLE
    }
}

class DocAdapter(
        private val docList: List<Doc>,
        private val listener: Listener
) : RecyclerView.Adapter<DocAdapter.DocViewHolder>() {

    interface Listener {
        fun onClicked(doc: Doc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        return DocViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_doc_l, parent, false))
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        docList[position].let { doc ->
            holder.itemView.run {
                li_tv_doc_no.text = doc.docNo
                li_tv_location_code.text = doc.locationName
                setOnClickListener {
                    listener.onClicked(doc)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return docList.size
    }

    class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}