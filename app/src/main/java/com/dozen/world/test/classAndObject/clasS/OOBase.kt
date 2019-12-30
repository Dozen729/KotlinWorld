package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-29.
 * Describe:
 *
 *
 *
 */
class OOBase(p:Int): OBase(p.also { println("top 11111") }) {


    init {
        this.openClass="open class Derived".also{ println(" init 22222")}
    }

    override fun draw() {
        super.draw()
        println("open fun derived")

    }

    override val openVal="val derived".also { println("override 33333") }

    val openVall="not override".also { println("val 4444444") }

}