package moy.test

import org.junit.Test
import org.otnip.moy.core.MoyServiceImpl

@MoyServiceImpl(impl="moy.test.HelloWorldServiceImpl")
interface HelloWorldService {
    fun hello(x:String) : String

    fun get() : TestObject
}

class TestObject {
    var x : String = ""
    var y : Long = 9L
}