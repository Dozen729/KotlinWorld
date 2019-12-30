package com.dozen.world.test.classAndObject.basic

import java.lang.Integer.parseInt

/**
 * Created by Hugo on 19-9-25.
 * Describe:
 *
 *
 *
 */

fun main() {

    val a = 100
    val b = 50

    // 传统用法
    var max1 = a
    if (a < b) max1 = b

// With else
    val max2: Int
    if (a > b) {
        max2 = a
    } else {
        max2 = b
    }

// 作为表达式
    val max3 = if (a > b) a else b

    println("max1:$max1 max2:$max2 max3:$max3")

    val max4 = if (a > b) {
        print("Choose a")
        a
    } else {
        print("Choose b")
        b
    }
    println(max4)

    var x: Int = 17

    when (x) {
        1 -> print("x == 1")
        2 -> print("x == 2")
        else -> { // 注意这个块
            print("x is neither 1 nor 2")
        }
    }

    val validNumbers = Array(3) { 15 }

    validNumbers[0] = 16
    validNumbers[1] = 19
    validNumbers[2] = 18

    validNumbers.forEach { println("$it hhh") }

    println()

    when (x) {
        in 1..10 -> print("x is in the range")
        in validNumbers -> print("x:$x is valid")
        !in 10..20 -> print("x is outside the range")
        else -> print("none of the above")
    }
    println()

    when (x) {
        0, 1 -> print("x == 0 or x == 1")
        else -> print("otherwise")
    }
    println()

    when (x) {
        parseInt("17") -> print("s encodes x")
        else -> print("s does not encode x")
    }

    println()
    fun hasPrefix(x: Any) = when (x) {
        is String -> x.startsWith("prefix")
        else -> false
    }

    val st = "prefix is string"

    val tes = hasPrefix(st)
    println(tes)

    for (i in 1..3) {
        print("$i _")
    }
    println()

    for (i in 6 downTo 0 step 2) {
        print("$i _")
    }
    println()

    val array = validNumbers

    for (i in array.indices) {
        print("${array[i]} _")
    }
    println()

    for ((index, value) in array.withIndex()) {
        print("the element at $index is $value  ")
    }
    println()

    while (x > 0) {
        x--
        print("$x _")
    }
    println()

}