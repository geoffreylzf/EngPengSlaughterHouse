package my.com.engpeng.epslaughterhouse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_temp_slaughter_detail.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.TempSlaughterDetail
import my.com.engpeng.epslaughterhouse.util.format2Decimal

class TempSlaughterDetailAdapter(private val isShort: Boolean)
    : RecyclerView.Adapter<TempSlaughterDetailAdapter.ItemViewHolder>() {

    private var tempList: List<TempSlaughterDetail>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_temp_slaughter_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        tempList!![position].let { temp ->
            holder.itemView.run {
                li_tv_no.text = (itemCount - position).toString()
                li_tv_weight.text = temp.weight.format2Decimal()
                li_tv_qty.text = temp.qty.toString()
                li_tv_cage.text = temp.cage.toString()
                li_tv_cover.text = temp.cover.toString()
                tag = Pair(temp.id, temp.weight)

                if (isShort) {
                    li_tv_qty.visibility = View.GONE
                    li_tv_cage.visibility = View.GONE
                    li_tv_cover.visibility = View.GONE
                }

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

    fun setList(tempList: List<TempSlaughterDetail>) {
        this.tempList = tempList
        this.notifyDataSetChanged()
    }

    fun refresh(){
        this.notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}