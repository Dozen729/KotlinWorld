package com.dozen.world.test.classAndObject

import com.dozen.world.test.classAndObject.t.Color

/**
 * Created by Hugo on 19-12-24.
 * Describe:
 *
 *
 *
 */

fun main() {

    basicDataType()

    conditionControl()

    loopControl()

    tShow(Int)

    enumShow()



}

fun basicDataType() {


    val double1: Double = 123.6e10
    val float1: Float = 123.5f
    val long1: Long = 999_999_999L
    val int1: Int = 2147483647
    val short1: Short = 32767
    val byte1: Byte = 65

    //比较两个数字
    pn(short1 === short1) //值相等，对象地址相等
    val boxedA: Int? = int1
    val anotherBoxedA: Int? = int1
    //虽然经过了装箱，但是值是相等的
    pn(boxedA === anotherBoxedA)//false,值相等，对象地址不一样
    pn(boxedA == anotherBoxedA)//true,值相等

    //类型转换
    val int2: Int = byte1.toInt()
    val long2: Long = int1.toLong()
    val char1: Char = byte1.toChar()
    pp("toInt", int2)
    pp("toLong", long2)
    pp("toChar", char1)

    //位操作符
    val int3: Int = 3
    val int5: Int = -3
    val boolean: Boolean = true
    val boolean1: Boolean = false
    val boolean2: Boolean = true
    pp("shl", int3.shl(1))
    pp("shr", int3.shr(1))
    pp("ushr", int5.ushr(1))
    pp("or", int3.or(int5))
    pp("or", boolean.or(boolean1))
    pp("and", int3.and(int3))
    pp("and", boolean.and(boolean2))
    pp("inv", int3.inv())


    //数组
    val array1 = arrayOf(1, 2, 3)
    val array2 = Array(5) { i -> i * 2 }
    array1.iterator().forEach { p(it) }
    pn(" ")
    array2.iterator().forEach { p(it) }
    pn("")
    val array3: ByteArray = byteArrayOf(5, 6, 7)
    val array5: CharArray = charArrayOf('a', 'A', '0')
    array3.iterator().forEach { p(it) }
    pn("")
    array5.iterator().forEach { p(it) }
    pn("")


    //字符串
    val text1 = "hello kotlin"
    val text2 = """
        1.one
        2.two
        3.three
    """.trimIndent()
    val text3 = """
            a1.111
          a2.222a
        a3.333
    """.trimMargin("a")
    val text5 = """
        ${'$'}9.99
        99${'%'}
    """.trimIndent()
    pn(text1)
    pn(text2)
    pn(text3)
    pn(text5)

}

//if when
fun conditionControl() {

    val a = 30
    val b = (Math.random() * 50 + 1).toInt() - 20
    var max = a
    if (a < b) max = b

    var max1: Int
    if (a > b) {
        max1 = a
    } else {
        max1 = b
    }

    val max2 = if (a > b) a else b

    pp("max", max)
    pp("max1", max1)
    pp("max2", max2)

    //使用区间
    val max3 = if (b in 20..40) "在区间内" else "在区间外"
    pp("in $b", max3)


    //when表达式
    when (b) {
        1 -> pn("b==1")
        2 -> pn("b==2")
        20, 32 -> pn("20or32")
        else -> {
            pn("hello")
            pn("b不是1,也不是2")
        }
    }
    when (b) {
        in 1..10 -> pn("b is in the range")
        !in 10..20 -> pn("b is outside the range")
        else -> pn("none of the above")
    }
    val array1 = arrayOf(1, 5, 9, 20, 32, 8)
    val string1 = setOf("hello", "kotlin", "android")
    when {
        b in array1 -> pn("$b in ${array1.iterator().forEach { p(it) }}")
        b in array1 -> pn("true")
        "kotlin" in string1 -> pn("true")
    }

}

fun loopControl() {

    val string1 = listOf("hello", "kotlin", "world", "android", "dozen")
    for (i in string1) p(i)
    pn("")

    for ((index, value) in string1.withIndex()) pp(index, value)

    for (index in string1.indices) p("index:$index value:${string1[index]}")

    pn("")
    var n = 1
    while (n in 1..10) {
        n += 1
        p(string1.iterator().next())
    }
    pn("")
    n = 1
    do {
        n += 1
        p(string1.iterator().next())
    } while (n in 1..10)


    pn("")
    for (i in 1..10) {
        if (i == 3) continue
        p(i)
        if (i > 5) break
    }
    pn("")
    for (i in 1..10){
        for (j in 1..10){
            if (i+j>9)break
            print("${i+j}")
        }
        print(" ")
    }
    pn("")
    loop@for(i in 1..10){
        for (j in 1..10){
            if (1+j>9)break@loop
            print("${i+j}")
        }
        print(" ")
    }
    pn("")
    string1.forEach lit@{
        if (it=="world") return@lit
        p(it)
    }
    pn("")
    string1.forEach {
        if (it=="world")return@forEach
        p(it)
    }
    pn("")
    string1.forEach {
        if (it=="world")return
        p(it)
    }
    pn("")


}

fun p(value: Any) {
    print("  $value")
}

fun <T> tShow(content:T){
    when(content){
        is Int-> pn("int")
        is String-> pn("string")
        else-> pn("other")
    }
}

fun enumShow(){
    var color:Color=Color.BLUE

    pn(Color.values())
    pn(Color.valueOf("RED"))
    pn(color.name)
    pn(color.ordinal)

}

fun pn(value: Any) {
    println(value)
}

fun pp(detail: Any, value: Any) {

    val st: StringBuilder = java.lang.StringBuilder()
    st.append(detail).append(":").append(value)
    println(st.toString())

    st.reverse()
}