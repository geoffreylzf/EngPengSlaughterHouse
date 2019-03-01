package my.com.engpeng.epslaughterhouse.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_hk_table_info.*
import my.com.engpeng.epslaughterhouse.R
import my.com.engpeng.epslaughterhouse.db.BaseDao
import my.com.engpeng.epslaughterhouse.db.TableLogDao
import my.com.engpeng.epslaughterhouse.model.BaseEntity

class HkTableInfoFragment<D : BaseDao<E>, E : BaseEntity> : Fragment() {

    var entity: E? = null
    var dao: D? = null
    var tableLogDao: TableLogDao? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hk_table_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao!!.getLiveCount().observe(this,
                Observer { int ->
                    tv_count.text = "${entity?.displayName} ($int)"
                }
        )
        tableLogDao!!.getLiveByModel(entity!!.tableName).observe(this,
                Observer { tableLog ->
                    tableLog?.run {
                        tv_last_sync.text = "Last Synchronize : $lastSyncDate"
                        tv_progress.text = "$insert/$total (${insert.toDouble() * 100 / total.toDouble()}%)"
                        pb_progress.progress = insert / total * 100
                    }
                }
        )
    }
}
