package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-30.
 * Describe:
 *
 *
 *
 */
class ISBase: SOBase() {
    override fun draw(){
        println("ISBase out")
    }

    override val borderColor:String get()="blue"

    inner class IBase{
        fun fill(){
            println("IBase fill")
        }

        fun outA(){
            super@ISBase.draw()
            fill()
            draw()
            println("Drawn a filled rectangle with color ${super@ISBase.borderColor}")
            println("ISBase color $borderColor")
        }
    }

}