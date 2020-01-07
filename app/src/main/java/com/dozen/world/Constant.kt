package com.dozen.world

import android.graphics.Color
import com.dozen.world.bean.RoundItem

/**
 * Created by Hugo on 20-1-7.
 * Describe:
 *
 *
 *
 */
class Constant {

    companion object {
        fun getDataList(): ArrayList<RoundItem> {
            val tipList = ArrayList<String>()
            tipList.add(NameTip.KOTLIN.name)
            tipList.add(NameTip.TEST.name)
            tipList.add(NameTip.TEST2.name)

            //扇形大小均分
            val size = 1f

            val dataList = ArrayList<RoundItem>()
            for ((i, v) in tipList.withIndex()) {
                when (v) {
                    NameTip.KOTLIN.name -> {
                        val item = RoundItem(
                            i,
                            "kotlin加载列表",
                            size,
                            Color.BLUE,
                            "com.dozen.world.face.MessageActivity",
                            "使用Kotlin语言实现ListView显示网络图片和文字"
                        )
                        dataList.add(item)
                    }
                    NameTip.TEST.name -> {
                        val item = RoundItem(
                            i,
                            NameTip.TEST.name,
                            size,
                            Color.RED,
                            "",
                            "样例太少，测试专用"
                        )
                        dataList.add(item)
                    }
                    NameTip.TEST2.name -> {
                        val item = RoundItem(
                            i,
                            "这是标题",
                            size,
                            Color.YELLOW,
                            "",
                            "少少少少少少少少少少少少少少少少少少,英文显示未实现,English display not implemented"
                        )
                        dataList.add(item)
                    }
                }
            }

            return dataList
        }
    }


}