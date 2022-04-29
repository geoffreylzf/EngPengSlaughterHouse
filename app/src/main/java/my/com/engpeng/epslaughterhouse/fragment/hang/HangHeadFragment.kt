package my.com.engpeng.epslaughterhouse.fragment.hang


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_hang_head.*
import kotlinx.android.synthetic.main.fragment_hang_head.btn_start
import kotlinx.android.synthetic.main.fragment_hang_head.et_doc_no
import kotlinx.android.synthetic.main.fragment_hang_head.fab_refresh
import kotlinx.android.synthetic.main.fragment_hang_head.fab_scan
import kotlinx.android.synthetic.main.fragment_rece_head.*
import kotlinx.android.synthetic.main.list_item_doc_l.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.camera.CameraPermission
import my.com.engpeng.epslaughterhouse.camera.ScanActivity
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Doc
import my.com.engpeng.epslaughterhouse.model.ScanHangData
import my.com.engpeng.epslaughterhouse.model.ShHang
import my.com.engpeng.epslaughterhouse.util.I_KEY_SCAN_TEXT
import my.com.engpeng.epslaughterhouse.util.RC_SCAN
import my.com.engpeng.epslaughterhouse.util.hideKeyboard
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class HangHeadFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val vm: HangHeadViewModel by viewModel()
    private var hang = ShHang()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            et_doc_no.isEnabled = !it
            til_sup_name.visibility = if (!it) View.GONE else View.VISIBLE
        })

        vm.liveIsQrScan.observe(this, androidx.lifecycle.Observer {
            et_doc_no.isEnabled = !it
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
                        et_sup_name.setText(doc.personSupplierCompanyName)
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
            et_sup_name.setText("")
            et_remark.setText("")
            tv_doc_id.text = ""
            vm.setIsSelected(false)
            et_uuid.setText("")
            vm.setIsQrScan(false)
            et_doc_no.requestFocus()
        }

        btn_start.setOnClickListener {
            start()
        }

        fab_scan.setOnClickListener {
            if (CameraPermission.check(requireActivity())) {
                startActivityForResult(Intent(context, ScanActivity::class.java), RC_SCAN)
            } else {
                CameraPermission.request(requireActivity())
            }
        }
    }

    private fun start() {
        hang.run {
            docId = tv_doc_id.text.toString().toLongOrNull()
            docNo = et_doc_no.text.toString()
            remark = et_remark.text.toString()
            shReceiveUuid = et_uuid.text.toString()
        }

        var message = ""
        hang.run check@{
            if (docNo == null || docNo!!.isEmpty()) {
                message = "Please enter document number"
                return@check
            }
        }

        if (message.isNotEmpty()) {
            AlertDialogFragment.show(
                fragmentManager!!,
                getString(R.string.dialog_title_error),
                message
            )
            return
        }

        activity?.hideKeyboard()
        findNavController().navigate(
            HangHeadFragmentDirections.actionHangHeadFragmentToHangSumFragment(
                hang
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                val scanText = data?.getStringExtra(I_KEY_SCAN_TEXT) ?: ""
                if (scanText.isNotEmpty()) {
                    try {
                        val scanData = ScanHangData(scanText)
                        et_doc_no.setText(scanData.docNo)
                        et_uuid.setText(scanData.uuid)
                        vm.setIsQrScan(true)

                    } catch (e: Exception) {
                        AlertDialogFragment.show(
                            fragmentManager!!,
                            getString(R.string.dialog_title_error),
                            getString(R.string.error_desc, e.message)
                        )
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                .inflate(R.layout.list_item_doc_l, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        docList[position].let { doc ->
            holder.itemView.run {
                li_tv_doc_no.text = doc.docNo
                li_tv_location_code.text = doc.personSupplierCompanyName
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