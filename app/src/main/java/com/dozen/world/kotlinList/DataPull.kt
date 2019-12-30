package com.dozen.world.kotlinList

import com.dozen.world.R
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Created by Hugo on 19-12-25.
 * Describe:
 *
 *
 *
 */
class DataPull(private val pcb: PullCallback) : Callback<ResponseBody> {
    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
        pcb.onFail()
    }

    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
        val s= response?.body()?.string()

        val jo=JSONObject(s)
        val ja=jo.getJSONArray("results")
        val list= arrayListOf<MessageItem>()
        try {
            for (i in 1..ja.length()){
                val jo=ja.get(i) as JSONObject
                val item=MessageItem(i,"net $i", jo.getString("createdAt"),R.mipmap.icon, jo.getString("url"))
                list.add(item)
            }

        }catch (e: JSONException){

        }catch (e: IOException){

        }
        pcb.onSuccess(list)

    }


    fun getNetData(url: String) {

//        val list = arrayListOf<MessageItem>()
//        for (i in 1..10) {
//            val item = MessageItem(i, "pull $url", "pull", R.mipmap.ic_launcher)
//            list.add(item)
//        }
//        pcb.onSuccess(list)

        val retrofit = Retrofit.Builder().baseUrl("http://gank.io")
            .client(OkHttpClient().newBuilder().build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val dp: NetService =
            retrofit.create(Class.forName("com.dozen.world.kotlinList.NetService")) as NetService

        val call = dp.messagePull(url)

        call.enqueue(this)


    }

}


