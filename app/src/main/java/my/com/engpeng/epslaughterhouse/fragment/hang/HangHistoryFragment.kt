package my.com.engpeng.epslaughterhouse.fragment.hang


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
import kotlinx.android.synthetic.main.fragment_hang_history.*
import kotlinx.android.synthetic.main.list_item_hang_history.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.ShHang
import my.com.engpeng.epslaughterhouse.util.formatYesNo
import org.koin.android.ext.android.inject

class HangHistoryFragment : Fragment() {

    private val appDb: AppDb by inject()

    private lateinit var rvAdapter: HangHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hang_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)

        rvAdapter = HangHistoryAdapter(object : HangHistoryAdapter.Listener {
            override fun onClicked(hangId: Long) {
                findNavController().navigate(HangHistoryFragmentDirections.actionHangHistoryFragmentToHangHistoryDetailFragment(hangId))
            }
        })

        rv.adapter = rvAdapter

        appDb.shHangDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class HangHistoryAdapter(val listener: Listener) : RecyclerView.Adapter<HangHistoryAdapter.HangViewHolder>() {

    interface Listener {
        fun onClicked(hangId: Long)
    }

    private var hangList: List<ShHang>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HangViewHolder {
        return HangViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_hang_history, parent, false))
    }

    override fun getItemCount(): Int {
        return hangList?.size ?: 0
    }

    override fun onBindViewHolder(holder: HangViewHolder, position: Int) {
        hangList!![position].let { hang ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_doc_no.text = hang.docNo
                li_tv_remark.text = hang.remark
                li_tv_upload.text = hang.isUpload?.formatYesNo()

                if (hang.isDelete == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed50))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }

                setOnClickListener {
                    listener.onClicked(hang.id!!)
                }
            }
        }
    }

    fun setList(hangList: List<ShHang>) {
        this.hangList = hangList
        this.notifyDataSetChanged()
    }

    class HangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}