package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dialog_company.*
import kotlinx.android.synthetic.main.list_item_company.view.*
import kotlinx.coroutines.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.model.Company
import org.koin.android.ext.android.inject

class CompanyDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun show(fm: FragmentManager, listener: Listener) {
            return CompanyDialogFragment().apply {
                this.listener = listener
            }.show(fm, TAG)
        }
    }

    interface Listener {
        fun onSelected(companyId: Long)
    }

    private val appDb: AppDb by inject()
    private lateinit var listener: Listener


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            val companyList = appDb.companyDao().getAll()

            withContext(Dispatchers.Main) {
                if (companyList.isNotEmpty()) {
                    rv.layoutManager = LinearLayoutManager(context)
                    rv.adapter = CompanyDialogAdapter(companyList, object : CompanyDialogAdapter.Listener {
                        override fun onClicked(companyId: Long) {
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(200)
                                withContext(Dispatchers.Main) {
                                    listener.onSelected(companyId)
                                    dismiss()
                                }
                            }
                        }
                    })
                } else {
                    tv_title.setText(R.string.dialog_title_no_company)
                }
            }
        }
    }

}

class CompanyDialogAdapter(
        private val companyList: List<Company>,
        private val listener: Listener)
    : RecyclerView.Adapter<CompanyDialogAdapter.CompanyViewHolder>() {

    interface Listener {
        fun onClicked(companyId: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_company, parent, false))
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        companyList[position].let { company ->
            holder.itemView.run {
                li_tv_company_code.text = company.companyCode
                li_tv_company_name.text = company.companyName

                setOnClickListener {
                    listener.onClicked(company.id!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
