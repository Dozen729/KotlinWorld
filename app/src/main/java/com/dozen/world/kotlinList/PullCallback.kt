package com.dozen.world.kotlinList

/**
 * Created by Hugo on 19-12-25.
 * Describe:
 *
 *
 *
 */
interface PullCallback {
    fun pull(url:String)

    fun onSuccess(data:List<MessageItem>)

    fun onFail()
}