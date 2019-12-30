package com.dozen.world.test.classAndObject.interfacE

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
class A(override val name: String) :Named,MyInterface {

    override fun foo() {
        super<Named>.foo()
        super<MyInterface>.foo()

        println("A foo")
    }

    override fun bar() {

    }
}