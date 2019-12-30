package com.dozen.world.test.classAndObject.interfacE

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
interface Person:Named {

    val sex:String
    val cite:String

    override val name: String
        get() = "Named $sex $cite"

    override fun foo(){
        println("Person interface foo ")
    }
}