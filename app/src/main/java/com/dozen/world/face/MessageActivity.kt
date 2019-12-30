package com.dozen.world.face

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.dozen.world.R
import com.dozen.world.kotlinList.MessageAdapter
import com.dozen.world.kotlinList.MessageCallback
import com.dozen.world.kotlinList.MessageItem
import com.dozen.world.kotlinList.MessagePresenter
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() , AdapterView.OnItemClickListener , MessageCallback{
    override fun <T> pullData(data: T) {
        this.data.addAll(data as List<MessageItem>)
        adapter.notifyDataSetChanged()
    }

    var mp=MessagePresenter(this)

    var data = arrayListOf<MessageItem>()
    lateinit var adapter: MessageAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        for (i in 1..10) {
            val item = MessageItem(i, "name $i", "content $i", R.mipmap.icon,"")
            data.add(item)
        }

        adapter = MessageAdapter(data, this)

        message_list.adapter = adapter

        message_list.onItemClickListener =listener

        message_list.setOnItemLongClickListener{a,v,i,l-> kotlin.run {

            val name=v.findViewById<TextView>(R.id.message_name)

            Toast.makeText(baseContext," ${name.text} id:$i id:$l", Toast.LENGTH_SHORT).show()

            name.text="hello world"

            return@run true
        }}

//        list.setOnItemClickListener { adapterView, view, i, l -> print("hello") }

//        list.onItemClickListener=this

        btn_add_net_data.setOnClickListener { mp.pull("20/${(Math.random()*30+1).toInt()}") }

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    var listener= AdapterView.OnItemClickListener{ a, v, i, l->kotlin.run {
        Toast.makeText(baseContext," ${v.findViewById<TextView>(R.id.message_name).text} id:$i id:$l",
            Toast.LENGTH_SHORT).show()
    }}
}
