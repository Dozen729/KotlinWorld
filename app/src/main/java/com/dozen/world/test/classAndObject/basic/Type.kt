package com.dozen.world.test.classAndObject.basic

/**
 * Created by Hugo on 19-9-25.
 * Describe:
 *
 *
 *
 */
fun main() {
    println("hello kotlin")


    val oneInt = 1
    val threeBillion = 3000000000//Long
    val oneLong = 10L//Long
    val oneByte: Byte = 5


    val pi = 3.14
    val e = 2.7182818284

    val oneMillion = 1_000_000//Long
    val creditCardNumber = 1234_5678_9012_3456L//Long
    val socialSecurityNumber = 999_99_9999L//Long
    val hexBytes = 0xFF_EC_DE_5E//16进制
    val bytes = 0b11010010_01101001_10010100_10010010

    val test = oneMillion * oneLong
    println(creditCardNumber)
    println(test)

    val a: Int = 10000
    println(a === a) // 输出“true”
    val boxedA: Int? = a
    val anotherBoxedA: Int? = a
    println(boxedA === anotherBoxedA) // ！！！输出“false”！！！数字装箱不一定保留同一性
    println(boxedA == anotherBoxedA)// 输出“true” 保留了相等性


    val toI: Int = creditCardNumber.toInt()
//    toByte(): Byte
//    toShort(): Short
//    toInt(): Int
//    toLong(): Long
//    toFloat(): Float
//    toDouble(): Double
//    toChar(): Char
    println(creditCardNumber)
    println(toI)

    val l = 1L + 3 // Long + Int => Long

    val y = (1 shl 2) and 0x000FF000
//    shl(bits) – 有符号左移
//    shr(bits) – 有符号右移
//    ushr(bits) – 无符号右移
//    and(bits) – 位与
//    or(bits) – 位或
//    xor(bits) – 位异或
//    inv() – 位非

//    相等性检测：a == b 与 a != b
//    比较操作符：a < b、 a > b、 a <= b、 a >= b
//    区间实例以及区间检测：a..b、 x in a..b、 x !in a..b


//            || – 短路逻辑或
//            && – 短路逻辑与
//            ! - 逻辑非

    //数组
    val asc = Array(5) { i -> (i * i).toString() }
    asc.forEach { print("$it---") }

    val x: IntArray = intArrayOf(1, 2, 3)
    x[0] = x[1] + x[2]
// Array of int of size 5 with values [0, 0, 0, 0, 0]
    val arr1 = IntArray(5)

// e.g. initialise the values in the array with a constant
// Array of int of size 5 with values [42, 42, 42, 42, 42]
    val arr2 = IntArray(5) { 42 }

// e.g. initialise the values in the array using a lambda
// Array of int of size 5 with values [0, 1, 2, 3, 4] (values initialised to their index value)
    val arr3 = IntArray(5) { it * 1 }

    arr3.forEach { print("$it _") }


    val s = "Hello, world!\n"
    val text = """
        for (c in "foo")
            print(c)
        """.trim()
    val text1 = """
    |   Tell me and I forget.
        |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
    """.trimMargin()
    println("$s+Dozen")
    println(text)
    println(text1)

    val s2 = "abc"
    println("$s2.length is ${s2.length}") // 输出“abc.length is 3”

    val price = """
${'$'}9.99
"""

    println(price)

}


