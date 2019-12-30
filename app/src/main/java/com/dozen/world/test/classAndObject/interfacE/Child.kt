package com.dozen.world.test.classAndObject.interfacE

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
class Child:MyInterface {
    override fun bar() {
        println("interface bar")
    }

    override fun foo() {
        super.foo()
        println("class foo")
    }

}