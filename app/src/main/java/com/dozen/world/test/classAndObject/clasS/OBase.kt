package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-29.
 * Describe:
 *
 *
 *
 */
open class OBase(p:Int) {
    var openClass="open class"

    open val openVal="base open val"

    open fun draw(){
        println(openVal)
    }

    fun fill(){
        println("OBase fill")
    }

}