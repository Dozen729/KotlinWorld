package com.dozen.world.kotlinList

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Hugo on 19-12-25.
 * Describe:
 *
 *
 *
 */
interface NetService {

    @GET("api/data/福利/{data}")
    fun messagePull(@Path("data")data:String):Call<ResponseBody>

}