package com.dozen.world.test.classAndObject.basic

/**
 * Created by Hugo on 19-9-26.
 * Describe:
 *
 *
 *
 */
fun main() {
    println("foo")
    foo()
    println()

    println("foo2")

    foo2()
    println()

    println("foo3")
    foo3()
    println()

    println("foo5")
    foo5()
    println()

    println("foo6")
    foo6()


}


var testArray=listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

fun foo() {
    testArray.forEach {
        if (it == 3) return // 非局部直接返回到 foo() 的调用者
        print("$it--")
    }
    println("this point is unreachable")
}

fun foo2() {
    testArray.forEach lit@{
        if (it == 5) return@lit // 局部返回到该 lambda 表达式的调用者，即 forEach 循环
        print("$it--")
    }
    print(" done with explicit label")
}

fun foo3() {
    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forEach {
        if (it == 7) return@forEach // 局部返回到该 lambda 表达式的调用者，即 forEach 循环
        print("$it--")
    }
    print(" done with implicit label")
}

fun foo5() {
    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forEach(fun(value: Int) {
        if (value == 9) return  // 局部返回到匿名函数的调用者，即 forEach 循环
        print("$value--")
    })
    print(" done with anonymous function")
}

fun foo6() {
    run loop@{
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).forEach {
            if (it == 7) return@loop // 从传入 run 的 lambda 表达式非局部返回
            print("$it--")
        }
    }
    print(" done with nested loop")
}