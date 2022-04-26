package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dialog_history_mortality.*
import kotlinx.android.synthetic.main.list_item_trip_mortality.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.ShReceiveMortality
import my.com.engpeng.epslaughterhouse.util.format2Decimal

class HistoryMortalityDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun show(fm: FragmentManager,
                 shReceiveMortalityList: List<ShReceiveMortality>): HistoryMortalityDialogFragment {
            return HistoryMortalityDialogFragment().apply {
                this.shReceiveMortalityList = shReceiveMortalityList
                show(fm, TAG)
            }
        }
    }

    private lateinit var shReceiveMortalityList: List<ShReceiveMortality>

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(600, 600)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_history_mortality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = HistoryMortalityDialogAdapter(shReceiveMortalityList)
    }
}

class HistoryMortalityDialogAdapter(
        private val mortalityList: List<ShReceiveMortality>)
    : RecyclerView.Adapter<HistoryMortalityDialogAdapter.MortalityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MortalityViewHolder {
        return MortalityViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_trip_mortality, parent, false))
    }

    override fun onBindViewHolder(holder: MortalityViewHolder, position: Int) {
        mortalityList[position].let { mortality ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = mortality.weight.format2Decimal()
                li_tv_qty.text = mortality.qty.toString()

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mortalityList.size
    }

    class MortalityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}