package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-30.
 * Describe:
 *
 *
 *
 */
abstract class ABase(p:Int): OBase(p) {
    abstract override fun draw()

    abstract fun test()

    val testVA="abstract VAL test"

    open fun testP(){
        println("abstract class test")
    }
}