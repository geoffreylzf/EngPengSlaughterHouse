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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dialog_bluetooth.*
import kotlinx.android.synthetic.main.list_item_bluetooth.view.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.model.Bluetooth
import java.util.concurrent.TimeUnit

class BluetoothDialogFragment : DialogFragment() {

    companion object {
        val TAG = this::class.qualifiedName
        fun show(fm: FragmentManager,
                 nameList: List<String>,
                 addressList: List<String>,
                 listener: Listener): BluetoothDialogFragment {
            return BluetoothDialogFragment().apply {
                this.nameList = nameList
                this.addressList = addressList
                this.listener = listener
                show(fm, TAG)
            }
        }
    }

    interface Listener {
        fun onSelect(bluetooth: Bluetooth)
    }

    private lateinit var nameList: List<String>
    private lateinit var addressList: List<String>
    private lateinit var listener: Listener

    private var compositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(600, 600)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_bluetooth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = BluetoothDialogAdapter(nameList, addressList).apply {
            clickEvent
                    .subscribeOn(Schedulers.io())
                    .delay(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        listener.onSelect(it)
                        dismiss()
                    }.addTo(compositeDisposable)
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}

class BluetoothDialogAdapter(
        private val nameList: List<String>,
        private val addressList: List<String>
) : RecyclerView.Adapter<BluetoothDialogAdapter.BluetoothViewHolder>() {

    private val clickSubject = PublishSubject.create<Bluetooth>()
    val clickEvent: Observable<Bluetooth> = clickSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_bluetooth, parent, false))
    }

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        val name = nameList[position]
        val address = addressList[position]

        holder.itemView.run {
            clicks().map { Bluetooth(name, address) }.subscribe(clickSubject)
            li_bt_name.text = name
            li_bt_address.text = address
        }
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}