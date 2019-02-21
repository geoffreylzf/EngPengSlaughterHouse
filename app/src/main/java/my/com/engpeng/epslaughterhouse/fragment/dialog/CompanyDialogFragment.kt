package my.com.engpeng.epslaughterhouse.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_company.*
import kotlinx.android.synthetic.main.list_item_company.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.di.AppModule
import my.com.engpeng.epslaughterhouse.model.Company
import java.util.concurrent.TimeUnit

class CompanyDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun getInstance(fm: FragmentManager): CompanyDialogFragment {
            return CompanyDialogFragment().apply {
                show(fm, TAG)
            }
        }
    }

    private val appDb by lazy { AppModule.provideDb(requireContext()) }
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null

    private val selectSubject = PublishSubject.create<Company>()
    val selectEvent: Observable<Company> = selectSubject

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        disposable = appDb.companyDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        rv.layoutManager = LinearLayoutManager(context)
                        rv.adapter = CompanyDialogAdapter(it).apply {
                            disposable2 = clickEvent
                                    .subscribeOn(Schedulers.io())
                                    .delay(200, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { company ->
                                        selectSubject.onNext(company)
                                        dismiss()
                                    }
                        }
                    } else {
                        tv_title.setText(R.string.dialog_title_no_company)
                    }
                }

        return inflater.inflate(R.layout.fragment_dialog_company, container, false)
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
        disposable2?.dispose()
    }
}

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
