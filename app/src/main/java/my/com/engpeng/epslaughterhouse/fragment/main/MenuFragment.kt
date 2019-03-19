package my.com.engpeng.epslaughterhouse.fragment.main


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.list_item_doc.view.*
import kotlinx.android.synthetic.main.merge_menu_doc.*
import kotlinx.android.synthetic.main.merge_menu_log.*
import kotlinx.android.synthetic.main.merge_menu_trip.*
import kotlinx.coroutines.*
import my.com.engpeng.epslaughterhouse.MainActivity
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Doc
import my.com.engpeng.epslaughterhouse.service.GetDocService
import my.com.engpeng.epslaughterhouse.service.UploadService
import my.com.engpeng.epslaughterhouse.util.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment() {

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()
    private var rvAdapter = DocAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setNavHeader()
        setupView()
        setupListener()
        setupRv()
    }

    private fun setupView() {
        tv_version.text = context?.appVersion()
        appDb.tripDao().getLiveCountByUpload(0).observe(this, Observer {
            tv_upload_count.text = it.toString()
        })

        appDb.logDao().getLiveLastLogByTask(LOG_TASK_UPLOAD).observe(this, Observer {
            it?.let { log ->
                tv_upload_dt.text = log.datetime
                tv_upload_msg.text = log.remark
            }
        })

        appDb.tripDao().getLiveCountByDate(Sdf.getCurrentDate()).observe(this, Observer {
            tv_trip_confirm.text = (it.confirmCount ?: 0).toString()
            tv_trip_delete.text = (it.deleteCount ?: 0).toString()
        })
    }

    private fun setupListener() {
        btn_trip.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToTripHeadFragment())
        }

        btn_upload.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val count = appDb.tripDao().getCountByUpload(0)
                withContext(Dispatchers.Main) {
                    if (count == 0) {
                        AlertDialogFragment.show(fragmentManager!!,
                                getString(R.string.error),
                                getString(R.string.dialog_error_msg_no_data_upload))
                    } else {
                        val intent = Intent(activity, UploadService::class.java).apply {
                            putExtra(I_KEY_LOCAL, cb_local.isChecked)
                        }
                        activity!!.stopService(intent)
                        activity!!.startService(intent)
                    }
                }
            }
        }

        btn_doc.setOnClickListener {
            val intent = Intent(activity, GetDocService::class.java).apply {
                putExtra(I_KEY_LOCAL, cb_local.isChecked)
                putExtra(I_KEY_DATE, Sdf.getCurrentDate())
            }
            activity!!.stopService(intent)
            activity!!.startService(intent)
        }
    }

    private fun setupRv(){
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = rvAdapter
        appDb.docDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class DocAdapter : RecyclerView.Adapter<DocAdapter.DocViewHolder>() {

    private var docList: List<Doc>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        return DocViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_doc, parent, false))
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        docList!![position].let { doc ->
            holder.itemView.run {
                li_tv_doc_no.text = doc.docNo
                li_tv_location_code.text = doc.locationName
            }
        }
    }

    override fun getItemCount(): Int {
        return docList?.size ?: 0
    }

    fun setList(docList: List<Doc>) {
        this.docList = docList
        this.notifyDataSetChanged()
    }

    class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}