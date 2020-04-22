package com.dozen.world.face

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dozen.world.R
import com.dozen.world.bean.TopTabItem
import com.dozen.world.dao.SharesDBHelper
import kotlinx.android.synthetic.main.activity_shares_detail.*
import kotlinx.android.synthetic.main.item_top_tab.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by Hugo on 20-4-21.
 * Describe:
 *
 *
 *
 */
class SharesDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var item: TopTabItem
    private lateinit var sh: SharesDBHelper

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.shares_item_optional -> {
                item.optional = if (item.optional == 0) 1 else 0
                sh.update(item)
                itemStateChange()
            }
            R.id.shares_item_collection -> {
                item.collection = if (item.collection == 0) 1 else 0
                sh.update(item)
                itemStateChange()
            }
            R.id.shares_item_good -> {
                item.good = if (item.good == 0) 1 else 0
                sh.update(item)
                itemStateChange()

            }
            R.id.shares_item_bad -> {
                item.bad = if (item.bad == 0) 1 else 0
                sh.update(item)
                itemStateChange()
            }

            R.id.shares_detail_monthly -> {
                Glide.with(baseContext)
                    .load("http://image.sinajs.cn/newchart/monthly/n/${item.location}${item.code}.gif")
                    .into(shares_item_picture)
            }
            R.id.shares_detail_weekly -> {
                Glide.with(baseContext)
                    .load("http://image.sinajs.cn/newchart/weekly/n/${item.location}${item.code}.gif")
                    .into(shares_item_picture)
            }
            R.id.shares_detail_daily -> {
                Glide.with(baseContext)
                    .load("http://image.sinajs.cn/newchart/daily/n/${item.location}${item.code}.gif")
                    .into(shares_item_picture)
            }
            R.id.shares_detail_min -> {
                Glide.with(baseContext)
                    .load("http://image.sinajs.cn/newchart/min/n/${item.location}${item.code}.gif")
                    .into(shares_item_picture)
            }

            R.id.shares_delete -> {
                try {
                    sh.delete("code='${item.code}'")

                }finally {
                    finish()
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares_detail)

        val bundle = this.intent.extras
        item = bundle?.get("shares") as TopTabItem

        sh = SharesDBHelper(baseContext)

        doAsync {
            val requestStr =
                URL("http://hq.sinajs.cn/list=${item.location}${item.code}").readText(charset("GBK"))
            uiThread {

                val list = requestStr.split(",")
//                list.forEach { Log.d("testest", it) }

                val text = StringBuilder()
                text.append(list[0].split("\"")[1]).append("(${item.location}${item.code})")
                    .append("---")
                    .append("现价(${list[3]})").append("---")
                    .append("今开(${list[1]})").append("---").append("昨收(${list[2]})").append("---")
                    .append("最高(${list[4]})").append("---").appendln("最低(${list[5]})")
                    .append("买一(${list[10]})(${list[11]})").append("---")
                    .append("买二(${list[12]})(${list[13]})").append("---")
                    .append("买三(${list[14]})(${list[15]})").append("---")
                    .append("买四(${list[16]})(${list[17]})").append("---")
                    .appendln("买五(${list[18]})(${list[19]})")
                    .append("卖一(${list[20]})(${list[21]})").append("---")
                    .append("卖二(${list[22]})(${list[23]})").append("---")
                    .append("卖三(${list[24]})(${list[25]})").append("---")
                    .append("卖四(${list[26]})(${list[27]})").append("---")
                    .appendln("卖五(${list[28]})(${list[29]})")

                shares_detail_info.text = text
            }
        }

        shares_item_name.isVisible = false
        itemStateChange()


        Glide.with(baseContext)
            .load("http://image.sinajs.cn/newchart/monthly/n/${item.location}${item.code}.gif")
            .into(shares_item_picture)


        shares_item_optional.setOnClickListener(this)
        shares_item_collection.setOnClickListener(this)
        shares_item_good.setOnClickListener(this)
        shares_item_bad.setOnClickListener(this)

        shares_detail_monthly.setOnClickListener(this)
        shares_detail_weekly.setOnClickListener(this)
        shares_detail_daily.setOnClickListener(this)
        shares_detail_min.setOnClickListener(this)

        shares_delete.setOnClickListener(this)

    }

    private fun itemStateChange() {
        if (item.optional == 0) shares_item_optional.setBackgroundResource(R.drawable.shares_un_optional) else shares_item_optional.setBackgroundResource(
            R.drawable.shares_optional
        )
        if (item.collection == 0) shares_item_collection.setBackgroundResource(R.drawable.shares_un_collection) else shares_item_collection.setBackgroundResource(
            R.drawable.shares_collection
        )
        if (item.good == 0) shares_item_good.setBackgroundResource(R.drawable.shares_un_good) else shares_item_good.setBackgroundResource(
            R.drawable.shares_good
        )
        if (item.bad == 0) shares_item_bad.setBackgroundResource(R.drawable.shares_un_bad) else shares_item_bad.setBackgroundResource(
            R.drawable.shares_bad
        )
    }

}