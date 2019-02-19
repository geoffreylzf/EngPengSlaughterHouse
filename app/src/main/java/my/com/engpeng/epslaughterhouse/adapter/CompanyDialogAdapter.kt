package my.com.engpeng.epslaughterhouse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_item_company.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.Company

class CompanyDialogAdapter(
        private val companyList: List<Company>)
    : RecyclerView.Adapter<CompanyDialogAdapter.CompanyViewHolder>() {

    private val clickSubject = PublishSubject.create<Company>()
    val clickEvent: Observable<Company> = clickSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_company, parent, false))
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        companyList[position].let { company ->
            holder.itemView.apply {
                clicks().map { company }.subscribe(clickSubject)
            }.run {
                li_tv_company_code.text = company.companyCode
                li_tv_company_name.text = company.companyName
            }
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}