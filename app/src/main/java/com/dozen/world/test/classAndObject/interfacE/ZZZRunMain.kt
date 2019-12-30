package com.dozen.world.test.classAndObject.interfacE

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
fun main(){

//    test1()
//    test2()
    test3()
}

fun test3(){

    val cla=A("A")

    cla.foo()

}

fun test2(){

    val cla=Employee("nan","bao an")

    println(cla.info)
    println(cla.name)
    println(cla.cite)

    cla.info="Dozen"

    println(cla.name)
    println(cla.info)


}

fun test1(){

    val cla=Child()

    cla.bar()
    cla.foo()

}