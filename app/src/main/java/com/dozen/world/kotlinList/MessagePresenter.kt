package com.dozen.world.kotlinList

import com.dozen.world.face.MessageActivity

/**
 * Created by Hugo on 19-12-25.
 * Describe:
 *
 *
 *
 */
class MessagePresenter (private val ma: MessageActivity):PullCallback{


    private val dp=DataPull(this)


    override fun pull(url: String) {
        dp.getNetData(url)
    }

    override fun onSuccess(data: List<MessageItem>) {
        ma.pullData(data)
    }

    override fun onFail() {

    }
}