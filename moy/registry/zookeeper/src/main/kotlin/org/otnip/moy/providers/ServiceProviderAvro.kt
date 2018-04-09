package org.otnip.moy.providers

import org.otnip.moy.RegistryZk
import org.otnip.moy.core.MicroserviceInstance
import org.otnip.moy.registry.MicroserviceInstanceListener
import mu.KotlinLogging
import org.apache.avro.ipc.NettyTransceiver
import org.apache.avro.ipc.reflect.ReflectRequestor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.net.InetSocketAddress

class ServiceProviderAvro(private val clazz: Class<*>) : InvocationHandler, MicroserviceInstanceListener {

    private val logger = KotlinLogging.logger {}
    private val microserviceInstanceImpls = mutableListOf<MicroserviceInstanceImpl>()
    private var currentMicroserviceInstanceImplIndex = 0

    init {
        RegistryZk.observe("/${clazz.simpleName}", this)
    }

    private fun nextMicroserviceInstanceImpl(): MicroserviceInstanceImpl? {
        var output: MicroserviceInstanceImpl? = null
        synchronized(microserviceInstanceImpls) {
            if (microserviceInstanceImpls.isNotEmpty()) {
                currentMicroserviceInstanceImplIndex = (currentMicroserviceInstanceImplIndex + 1) % microserviceInstanceImpls.size
                output = microserviceInstanceImpls.get(currentMicroserviceInstanceImplIndex)
            }
        }
        return output
    }


    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        var microserviceInstanceImpl: MicroserviceInstanceImpl? = nextMicroserviceInstanceImpl()
        while (microserviceInstanceImpl == null) {
            logger.info { "waiting : ${clazz.name}.{$method.name}" }
            Thread.sleep(2500)

            //TODO:  be proactive if we have been waiting for a while
        }

        // make call
        try {
            return if ( args != null ) {
                method.invoke(microserviceInstanceImpl.reflectRequestor, *args)
            } else {
                method.invoke(microserviceInstanceImpl.reflectRequestor)
            }
        } catch (e: Exception) {
            // throw exception if appropriate
            if (e is java.lang.reflect.InvocationTargetException) {
                val targetException = e.targetException
                for (exceptionType in method.exceptionTypes) {
                    if (exceptionType.isAssignableFrom(targetException.javaClass)) {
                        throw targetException
                    }
                }
            }

            // disconnect if appropriate
            var tryAgain = false
//            synchronized(instances) {
//                var indexToRemove = -1
//                var i = 0
//                while (i < instances.size) {
//                    if (!transeivers.get(i).isConnected()) {
//                        indexToRemove = i
//                        i = instances.size
//                    }
//                    i++
//                }
//                if (indexToRemove != -1) {
//                    logger.info("removing instance...")
//                    tryAgain = true
//                    instances.removeAt(indexToRemove)
//                    transeivers.removeAt(indexToRemove)
//                    requestors.removeAt(indexToRemove)
//                }
//            }

            return if (tryAgain) {
                invoke(proxy, method, args)
            } else {
                throw e
            }
        }
    }

    override fun event(allInstances: List<MicroserviceInstance>) {
        synchronized(microserviceInstanceImpls) {
            // cleanup missing instances
            for (missingInstance in microserviceInstanceImpls.filter { it.microserviceInstance !in allInstances }) {
                logger.info { "missing instance : $missingInstance" }

                // close NettyTransceiver
                try {
                    missingInstance.nettyTransceiver.close()
                } catch (ex: Exception) {
                    logger.error { "unexpected" }
                }

                // remove from microserviceInstanceImpls
                microserviceInstanceImpls.removeAll { it.microserviceInstance == missingInstance.microserviceInstance }
            }

            // create new instances

            for (newInstance in allInstances.filter { x -> microserviceInstanceImpls.count { it.microserviceInstance != x } == 0 }) {
                logger.info { "new instance : $newInstance" }
                if (newInstance.port != null) {
                    try {
                        val nettyTransceiver = NettyTransceiver(InetSocketAddress(newInstance.host, newInstance.port))
                        val reflectRequestor = ReflectRequestor.getClient(clazz, nettyTransceiver)
                        microserviceInstanceImpls.add(MicroserviceInstanceImpl(newInstance, nettyTransceiver, reflectRequestor))
                    } catch (ex: Throwable) {
                        logger.error { "error creating new instance : $clazz" }
                    }
                }
            }
        }
    }

    data class MicroserviceInstanceImpl(
            val microserviceInstance: MicroserviceInstance,
            val nettyTransceiver: NettyTransceiver,
            val reflectRequestor: Any
    )
}