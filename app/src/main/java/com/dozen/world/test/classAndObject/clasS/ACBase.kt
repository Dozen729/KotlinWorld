package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-30.
 * Describe:
 *
 *
 *
 */
class ACBase(p:Int): ABase(p) {
    override fun draw() {
        println("ACBse draw")
    }

    override fun test() {
        println("ACBase test")
        draw()
        testP()
    }

    override fun testP(){
        super.testP()
        println(super.testVA)
    }
}