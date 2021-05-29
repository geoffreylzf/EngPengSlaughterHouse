package my.com.engpeng.epslaughterhouse.fragment.main


import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_upload.*
import kotlinx.android.synthetic.main.list_item_log.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.fragment.dialog.AlertDialogFragment
import my.com.engpeng.epslaughterhouse.model.Log
import my.com.engpeng.epslaughterhouse.service.UploadService
import my.com.engpeng.epslaughterhouse.util.I_KEY_LOCAL
import my.com.engpeng.epslaughterhouse.util.LOG_TASK_UPLOAD
import org.koin.android.ext.android.inject

class UploadFragment : Fragment() {

    private val appDb: AppDb by inject()
    private var count = 0
    private var rvAdapter = LogAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
        setupRv()
    }

    private fun setupView() {
        (cl as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        appDb.utilDao().getLiveUnuploadCount().observe(this, Observer {
            count = it
            tv_count.text = it.toString()
            if (count == 0) {
                pb.visibility = View.GONE
            }
        })
    }

    private fun setupListener() {
        btn_upload.setOnClickListener {
            if (count == 0) {
                AlertDialogFragment.show(fragmentManager!!,
                        getString(R.string.error),
                        getString(R.string.dialog_error_msg_no_data_upload))
            } else {
                pb.visibility = View.VISIBLE
                val intent = Intent(activity, UploadService::class.java).apply {
                    putExtra(I_KEY_LOCAL, cb_local.isChecked)
                }
                activity!!.stopService(intent)
                activity!!.startService(intent)
            }
        }
    }

    private fun setupRv() {
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = rvAdapter
        appDb.logDao().getLiveLogByTask(LOG_TASK_UPLOAD).observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    private var logList: List<Log>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_log, parent, false))
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        logList!![position].let { log ->
            holder.itemView.run {
                li_tv_remark.text = log.remark
                li_tv_datetime.text = log.datetime
            }
        }
    }

    override fun getItemCount(): Int {
        return logList?.size ?: 0
    }

    fun setList(logList: List<Log>) {
        this.logList = logList
        this.notifyDataSetChanged()
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}