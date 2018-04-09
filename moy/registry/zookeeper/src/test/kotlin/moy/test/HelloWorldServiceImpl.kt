package moy.test

class HelloWorldServiceImpl : HelloWorldService {
    override fun hello(x : String) : String {
        println("hello : $x")
        return "asdfhaskdfh"
    }

    override fun get(): TestObject {
        return TestObject()
    }
}