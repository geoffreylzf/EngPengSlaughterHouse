package my.com.engpeng.epslaughterhouse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_temp_slaughter_mortality.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.TempTripMortality
import my.com.engpeng.epslaughterhouse.util.format2Decimal

class TempSlaughterMortalityAdapter : RecyclerView.Adapter<TempSlaughterMortalityAdapter.ItemViewHolder>() {

    private var tempList: List<TempTripMortality>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_temp_slaughter_mortality, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        tempList!![position].let { temp ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = temp.weight.format2Decimal()
                li_tv_qty.text = temp.qty.toString()
                tag = Pair(temp.id, temp.weight)

                if ((itemCount - position) % 2 == 0) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryXLight))
                } else {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tempList?.count() ?: 0
    }

    fun setList(tempList: List<TempTripMortality>) {
        this.tempList = tempList
        this.notifyDataSetChanged()
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}