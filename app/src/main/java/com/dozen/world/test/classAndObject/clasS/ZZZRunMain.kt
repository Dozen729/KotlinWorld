package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-29.
 * Describe:
 *
 *
 *
 */
fun main(){

//    testPerson()

//    openClass()

//    testInner()

    testAbstract()
}

fun testAbstract(){
    val a= ACBase(789)

    a.test()
}

fun testInner(){
    val i= ISBase()

    val b=i.IBase()

    b.outA()
}

fun testPerson(){
    val p = Person("persion", "testU")
    p.FunTest()
    println(p.firstProperty)

    val p1= Person("person2", "upupup", "con")

}

fun openClass(){

    val b= OBase(123)

    println(b.openVal)
    b.draw()
    b.fill()

    println()
    println()

    val d= OOBase(456)

    println(d.openVal)
    d.draw()
    d.fill()


}