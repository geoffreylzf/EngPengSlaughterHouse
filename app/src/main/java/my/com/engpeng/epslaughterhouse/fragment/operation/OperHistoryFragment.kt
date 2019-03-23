package my.com.engpeng.epslaughterhouse.fragment.operation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_oper_history.*
import kotlinx.android.synthetic.main.list_item_oper_history.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Operation
import my.com.engpeng.epslaughterhouse.util.formatYesNo
import org.koin.android.ext.android.inject

class OperHistoryFragment : Fragment() {

    private val appDb: AppDb by inject()

    private lateinit var rvAdapter: OperHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oper_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)

        rvAdapter = OperHistoryAdapter(object : OperHistoryAdapter.Listener {
            override fun onClicked(operId: Long) {
                findNavController().navigate(OperHistoryFragmentDirections.actionOperHistoryFragmentToOperHistoryDetailFragment(operId))
            }
        })

        rv.adapter = rvAdapter

        appDb.operationDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class OperHistoryAdapter(val listener: Listener) : RecyclerView.Adapter<OperHistoryAdapter.OperViewHolder>() {

    interface Listener {
        fun onClicked(operId: Long)
    }

    private var operList: List<Operation>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperViewHolder {
        return OperViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_oper_history, parent, false))
    }

    override fun getItemCount(): Int {
        return operList?.size ?: 0
    }

    override fun onBindViewHolder(holder: OperViewHolder, position: Int) {
        operList!![position].let { oper ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_doc_no.text = oper.docNo
                li_tv_remark.text = oper.remark
                li_tv_upload.text = oper.isUpload?.formatYesNo()

                if (oper.isDelete == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed50))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }

                setOnClickListener {
                    listener.onClicked(oper.id!!)
                }
            }
        }
    }

    fun setList(operList: List<Operation>) {
        this.operList = operList
        this.notifyDataSetChanged()
    }

    class OperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}