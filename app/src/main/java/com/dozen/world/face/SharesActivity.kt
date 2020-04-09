package com.dozen.world.face

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dozen.world.R
import com.dozen.world.adapter.SharesAdapter
import com.dozen.world.bean.TopTabItem
import com.dozen.world.custom.TopTabClickListener
import com.kevin.delegationadapter.DelegationAdapter
import kotlinx.android.synthetic.main.activity_shares.*

/**
 * Created by Hugo on 20-3-23.
 * Describe:
 *
 *
 *
 */
class SharesActivity : AppCompatActivity() ,TopTabClickListener {
    override fun clickListener(i: Int) {
        Toast.makeText(baseContext,"click:$i",Toast.LENGTH_SHORT).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shares)

        Log.d("test","testt")

        val s = arrayListOf("test1", "test2", "test3","test4", "test5", "test6","test7", "test8", "test9","test10", "test11", "test12")

        var data=ArrayList<TopTabItem>()
        for (i in 0..10){
            data.add(TopTabItem(i,"601006","sh",
                optional = false,
                collection = 1==i/2,
                good = 1==i/3,
                bad = 1==i/5
            ))
        }


        shares_top.initSwitchData(s)

        shares_top.ttcl=this


        shares_recycler_view.layoutManager=LinearLayoutManager(this)

        val da=DelegationAdapter()
        da.addDelegate(SharesAdapter(baseContext))
        da.addDataItems(data)

        shares_recycler_view.adapter=da




    }

}