package com.dozen.world.test.classAndObject.interfacE

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
data class Employee(override val sex: String, override val cite: String) : Person {
    var info: String = ""
        get() = name
        set(value) {
            if (value != "name")
                field = value
        }
}