package com.dozen.world.test.classAndObject.t

/**
 * Created by Hugo on 19-12-26.
 * Describe:
 *
 *
 *
 */
enum class ProtocolState {
    WAITING{
        override fun signal()=TALKING
    },
    TALKING {
        override fun signal()=WAITING
    };


    abstract fun signal():ProtocolState
}