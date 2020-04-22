package com.dozen.world.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dozen.world.R
import com.dozen.world.bean.TopTabItem
import com.kevin.delegationadapter.AdapterDelegate

/**
 * Created by Hugo on 20-4-9.
 * Describe:
 *
 *
 *
 */
class SharesAdapter(var context:Context,var listener: SharesItemListener) : AdapterDelegate<TopTabItem, SharesAdapter.ViewHolder>() {




    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: TopTabItem) {
        holder.name.text = item.code

        Glide.with(context).load("http://image.sinajs.cn/newchart/monthly/n/${item.location}${item.code}.gif").into(holder.picture)

        if (item.optional == 0) holder.optional.setBackgroundResource(R.drawable.shares_un_optional) else holder.optional.setBackgroundResource(
            R.drawable.shares_optional
        )
        if (item.collection == 0) holder.collection.setBackgroundResource(R.drawable.shares_un_collection) else holder.collection.setBackgroundResource(
            R.drawable.shares_collection
        )
        if (item.good == 0) holder.good.setBackgroundResource(R.drawable.shares_un_good) else holder.good.setBackgroundResource(
            R.drawable.shares_good
        )
        if (item.bad == 0) holder.bad.setBackgroundResource(R.drawable.shares_un_bad) else holder.bad.setBackgroundResource(
            R.drawable.shares_bad
        )

    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_tab, parent, false)
        return ViewHolder(view,listener)
    }

    class ViewHolder(itemView: View,var listener: SharesItemListener) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        override fun onClick(p0: View?) {
            p0?.let { listener.onItemClick(it,position) }
        }

        var name: TextView = itemView.findViewById(R.id.shares_item_name)
        var picture: ImageView = itemView.findViewById(R.id.shares_item_picture)
        var optional: ImageView = itemView.findViewById(R.id.shares_item_optional)
        var collection: ImageView = itemView.findViewById(R.id.shares_item_collection)
        var good: ImageView = itemView.findViewById(R.id.shares_item_good)
        var bad: ImageView = itemView.findViewById(R.id.shares_item_bad)

        init {
            picture.setOnClickListener(this)
            optional.setOnClickListener(this)
            collection.setOnClickListener(this)
            good.setOnClickListener(this)
            bad.setOnClickListener(this)
        }

    }

}