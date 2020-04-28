package com.dozen.world.impl

/**
 * Created by Hugo on 20-4-28.
 * Describe:
 *
 *
 *
 */
interface CommonCallback {
    fun success(id:Int?=0)
    fun fail(id:Int?=0)
    fun result(data:String)
}