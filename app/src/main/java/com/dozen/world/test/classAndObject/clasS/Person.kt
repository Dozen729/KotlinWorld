package com.dozen.world.test.classAndObject.clasS

/**
 * Created by Hugo on 19-9-29.
 * Describe:
 *
 *
 *
 */
class Person constructor(name:String, test:String) {
    val firstProperty="First property:$name".also(::println)


    init {
        println("First initializer block that prints ${name}")
    }

    val secondProperty="Second property:${name.length}".also(::println)

    init {
        println("Second initializer block that prints ${name.length}")
    }

    private val testUp=test.toUpperCase()

    fun FunTest(){
        val te=testUp
        println(te)
    }

    var children:MutableList<Person> = mutableListOf()

    constructor(name: String,test: String,parent: Person):this(name,test){
        parent.children.add(this)
        println("constructor")
    }

    constructor(name:String,test: String,con2:String):this(name,test){
        println("This is constructor test:$con2")
    }

}