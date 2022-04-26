package my.com.engpeng.epslaughterhouse.fragment.trip


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
import kotlinx.android.synthetic.main.fragment_rece_history.*
import kotlinx.android.synthetic.main.list_item_rece_history.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.ShReceiveDisplay
import my.com.engpeng.epslaughterhouse.util.formatYesNo
import org.koin.android.ext.android.inject

class ReceHistoryFragment : Fragment() {

    private val appDb: AppDb by inject()

    private lateinit var rvAdapter: ReceHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_rece_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)

        rvAdapter = ReceHistoryAdapter(object : ReceHistoryAdapter.Listener {
            override fun onClicked(receId: Long) {
                findNavController().navigate(ReceHistoryFragmentDirections.actionReceHistoryFragmentToReceHistoryDetailFragment(receId))
            }
        })

        rv.adapter = rvAdapter

        appDb.shReceiveDao().getLiveAll().observe(this, Observer {
            rvAdapter.setList(it)
        })
    }
}

class ReceHistoryAdapter(val listener: Listener) : RecyclerView.Adapter<ReceHistoryAdapter.ReceViewHolder>() {

    interface Listener {
        fun onClicked(receId: Long)
    }

    private var receList: List<ShReceiveDisplay>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceViewHolder {
        return ReceViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_rece_history, parent, false))
    }

    override fun onBindViewHolder(holder: ReceViewHolder, position: Int) {
        receList!![position].let { rece ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_company.text = rece.companyCode
                li_tv_location.text = rece.locationCode
                li_tv_doc_date.text = rece.docDate
                li_tv_doc_no.text = "${rece.docType}-${rece.docNo}"
                li_tv_type.text = rece.type
                li_tv_upload.text = rece.isUpload?.formatYesNo()

                if (rece.isDelete == 1) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed50))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }

                setOnClickListener {
                    listener.onClicked(rece.id!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return receList?.size ?: 0
    }

    fun setList(slaughterList: List<ShReceiveDisplay>) {
        this.receList = slaughterList
        this.notifyDataSetChanged()
    }

    class ReceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}