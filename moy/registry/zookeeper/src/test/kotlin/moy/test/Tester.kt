package moy.test

import junit.framework.TestCase
import org.otnip.moy.ServiceDeployer
import org.otnip.moy.ServiceProvider


class Tester : TestCase() {

    fun deploy() {
        val x = ServiceDeployer.deploy(HelloWorldService::class.java)
        println(x)
    }

    fun client() {
        val x = ServiceProvider.get(HelloWorldService::class.java)
        (1..10).forEach {
            println(it)
            x.hello("rogersz")
            println(x.get())
        }

        System.exit(0)
    }

    fun testSomething() {
        deploy()

        client()

        assert(true)
    }
}
