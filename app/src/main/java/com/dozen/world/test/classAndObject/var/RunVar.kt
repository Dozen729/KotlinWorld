package com.dozen.world.test.classAndObject.`var`

/**
 * Created by Hugo on 19-10-9.
 * Describe:
 *
 *
 *
 */
fun main(){

    val add= Address()

    add.city="hi hi hi"

    val copyAddress = copyAddress(add)

    println(add.name+add.city)
    println(copyAddress.name+copyAddress.city)
    println(copyAddress.sex+"     "+add.sex)

    add.run()


    val abc = "Dozen"

    println(abc)

}

fun copyAddress(address: Address):Address{
    val result=Address()

    result.name=address.name
    result.street=address.street

//    result.info=address.info

    result.city="bao an"

    result.sex="change"


    return result
}