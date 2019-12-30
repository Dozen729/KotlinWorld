package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-30.
 * Describe:
 *
 *
 *
 */
open class SOBase {

    open fun draw() {
        println("SOBase override test")

        SBase().scan()


    }

    open val borderColor:String get()="black"


    open fun scan(){
        println("scan a SOBase")
    }

    class SBase: SOBase(){
        override fun scan() {
            super.scan()
            println("scan a SBase")
        }

        val scanColor:String get()=super.borderColor
    }

}