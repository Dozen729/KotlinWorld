package com.dozen.world.test.classAndObject.`var`

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
class Address {
    var name:String="Dozen"
    var street:String="Xi Xiang"
    var city:String = "Sheng Zheng"
    var state:String?=null
    var zip:String="123456"

    val info:String="this is a val String."

    var sex:String = "456"
        get()= field +"789"
        set(value){
            if (value != "123")
                field=value
        }

    companion object {
        const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"
    }


    @Deprecated(SUBSYSTEM_DEPRECATED)fun run(){
//        println("companion object.")
        print(SUBSYSTEM_DEPRECATED)
    }
}