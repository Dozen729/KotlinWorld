package com.dozen.world.kotlinList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dozen.world.R

/**
 * Created by Hugo on 19-12-25.
 * Describe:
 *
 *
 *
 */
class MessageAdapter(mData: List<MessageItem>, context: Context) : BaseAdapter() {

    var data = mData
    var layout: LayoutInflater? = LayoutInflater.from(context)
    private var mContext=context

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {

        val v = layout?.inflate(R.layout.item_message, null)

        val id = v?.findViewById<TextView>(R.id.message_id)
        id?.text = " ${data[p0].id}"

        val name = v?.findViewById<TextView>(R.id.message_name)
        name?.text = data[p0].name

        val context = v?.findViewById<TextView>(R.id.message_content)
        context?.text = data[p0].content

        val url=v?.findViewById<TextView>(R.id.message_tip)
        url?.text=data[p0].url

        val picture=v?.findViewById<ImageView>(R.id.message_picture) as ImageView
        if (data[p0].url.isNotEmpty()){
            Glide.with(mContext).load(data[p0].url).into(picture)
        }else{
            picture.background = mContext.getDrawable(data[p0].picture)
        }

        return v
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }


}