package com.dozen.world.face

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dozen.world.Constant
import com.dozen.world.R
import com.dozen.world.adapter.SharesAdapter
import com.dozen.world.adapter.SharesItemListener
import com.dozen.world.bean.TopTabItem
import com.dozen.world.custom.TopTabClickListener
import com.dozen.world.dao.SharesDBHelper
import com.kevin.delegationadapter.DelegationAdapter
import kotlinx.android.synthetic.main.activity_shares.*
import java.util.regex.Pattern

/**
 * Created by Hugo on 20-3-23.
 * Describe:
 *
 *
 *
 */
class SharesActivity : AppCompatActivity(), TopTabClickListener, SharesItemListener {


    private lateinit var sh: SharesDBHelper
    private lateinit var da: DelegationAdapter
    private lateinit var data: List<TopTabItem>
    private var showId=0
    private var addNumber = 0

    override fun onItemClick(v: View, p: Int) {

        when (v.id) {
            R.id.shares_item_optional -> {
                data[p].optional = if (data[p].optional == 0) 1 else 0
                sh.update(data[p])
                da.notifyDataSetChanged()

            }
            R.id.shares_item_collection -> {
                data[p].collection = if (data[p].collection == 0) 1 else 0
                sh.update(data[p])
                da.notifyDataSetChanged()
            }
            R.id.shares_item_good -> {
                data[p].good = if (data[p].good == 0) 1 else 0
                sh.update(data[p])
                da.notifyDataSetChanged()

            }
            R.id.shares_item_bad -> {
                data[p].bad = if (data[p].bad == 0) 1 else 0
                sh.update(data[p])
                da.notifyDataSetChanged()
            }
            else -> {
                openDetailMsg(data[p])
            }
        }
    }

    override fun clickListener(i: Int) {
        showId=i
        addDataList(i, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)

        sh = SharesDBHelper(baseContext)

        shares_top.initSwitchData(Constant.SharesTop)

        shares_top.ttcl=this

        shares_recycler_view.layoutManager=LinearLayoutManager(this)

        da = DelegationAdapter()
        da.addDelegate(SharesAdapter(baseContext, this))
        da.addDataItems(null)

        shares_recycler_view.adapter=da

        addDataToDB()


        shares_add_data.setOnClickListener {
            if (showId==0){
                addDataList(0, ++addNumber)
            }
        }

        shares_search.setOnClickListener {

            if (Pattern.compile("[0-9]{6}").matcher(shares_code.text.toString()).matches()) {
                //输入为6位数字
                val item = sh.queryByCode("${shares_code.text}")

                if (item.code.isNullOrEmpty()) {
                    addShares()
                } else {
                    openDetailMsg(item)
                }


            } else {
                Toast.makeText(baseContext, "代码错误", Toast.LENGTH_LONG).show()
            }


        }

    }

    private fun addDataToDB() {
        Thread {
            if (sh.dbisNull()) {
                try {
                    sh.deleteAll()
                    val data = ArrayList<TopTabItem>()
                    Constant.SharesSZ.forEachIndexed { index, value ->
                        data.add(
                            TopTabItem(
                                index,
                                value,
                                "sz",
                                0,
                                0,
                                0,
                                0
                            )
                        )
                    }
                    Constant.SharesSH.forEachIndexed { index, value ->
                        data.add(
                            TopTabItem(
                                index,
                                value,
                                "sh",
                                0,
                                0,
                                0,
                                0
                            )
                        )
                    }
                    sh.insert(data)
                } finally {
                    handler.sendEmptyMessage(0)
                }

            } else {
                handler.sendEmptyMessage(0)
            }

        }.start()
    }

    private val handler = Handler(Handler.Callback {
        run {
            addData()
        }
        false
    })

    private fun openDetailMsg(item: TopTabItem) {
        val intent =
            Intent(baseContext, Class.forName("com.dozen.world.face.SharesDetailActivity"))
        val bundle = Bundle()
        bundle.putSerializable("shares", item)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun addShares() {
        val codeET = EditText(this)
        codeET.hint = "code"
        codeET.text = shares_code.text

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("代码加入数据库")
        dialog.setView(codeET)
        dialog.setPositiveButton("加入到sh") { d, i ->
            val item=TopTabItem(0,codeET.text.toString(),"sh",0,0,0,0)
            sh.insert(item)
        }
        dialog.setNegativeButton("加入到sz") { d, i ->
            val item=TopTabItem(0,codeET.text.toString(),"sh",0,0,0,0)
            sh.insert(item)
        }
        dialog.setNeutralButton("取消"){d,i->

        }

        dialog.create().show()
    }


    private fun addData() {
        data = sh.queryByID(0, 30)
        Log.d("testtest", "code: ${data.size}")
        da.clearAllData()
        da.addDataItems(data as ArrayList<TopTabItem>)
        da.notifyDataSetChanged()
    }

    private fun addDataList(show_id: Int, add_number: Int) {
        when (show_id) {
            0 -> {
                if (add_number == 0) {
                    addData()
                } else {
                    val add = sh.queryByID(30 * add_number, 30)
                    (data as ArrayList<TopTabItem>).addAll(add)
                    da.addDataItems(add as ArrayList<TopTabItem>)
                    da.notifyDataSetChanged()
                }
            }
            1 -> {
                data = emptyList()
                data = sh.query("optional=1")
                da.clearAllData()
                da.addDataItems(data as ArrayList<TopTabItem>)
                da.notifyDataSetChanged()
            }
            2 -> {
                data = emptyList()
                data = sh.query("collection=1")
                da.clearAllData()
                da.addDataItems(data as ArrayList<TopTabItem>)
                da.notifyDataSetChanged()
            }
            3 -> {
                data = emptyList()
                data = sh.query("good=1")
                da.clearAllData()
                da.addDataItems(data as ArrayList<TopTabItem>)
                da.notifyDataSetChanged()
            }
            4 -> {
                data = emptyList()
                data = sh.query("bad=1")
                da.clearAllData()
                da.addDataItems(data as ArrayList<TopTabItem>)
                da.notifyDataSetChanged()
            }
        }
    }

}