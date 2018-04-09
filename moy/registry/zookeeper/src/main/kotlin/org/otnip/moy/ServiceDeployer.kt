package org.otnip.moy

import org.otnip.moy.core.MicroserviceInstance
import org.otnip.moy.core.BuildInfo
import org.otnip.moy.core.MoyServiceImpl
import mu.KotlinLogging
import org.apache.avro.ipc.NettyServer
import org.apache.avro.ipc.reflect.ReflectResponder
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.handler.execution.ExecutionHandler
import java.lang.reflect.Constructor
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

/**
 *
 * @author David Pinto
 */
object ServiceDeployer {

    private val logger = KotlinLogging.logger {}

    fun deploy(vararg classes: Class<*>) {
        // make sure all paths exist
        for (clazz in classes) {
            RegistryZk.initPath("/${clazz.simpleName.replace("\\.".toRegex(), "/")}")
        }

        // register with zookeeper and JVM metric monitoring
        for (clazz in classes) {
            val objectImpl = createObjectImpl(getClassImpl(clazz))!!
            val microserviceInstance = MicroserviceInstance(
                    clazz.simpleName,
                    InetAddress.getLocalHost().hostName,
                    deployAvro(clazz, objectImpl),
                    BuildInfo.timestamp,
                    BuildInfo.hash)

            val path = RegistryZk.register(microserviceInstance, false)
            logger.info { "deployed : $path" }
            // RegistryZk.registerShutdownHook(path)

            // start Resource Usage metrics reporting
            // val tokens = path.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            // JvmMetricsReporter(clazz.name + "." + tokens[tokens.size - 1]).run() // convert zookeeper path to graphite path (and remove leading "grouphigh.")
        }
    }


    fun deployAvro(clazzInterface: Class<*>, objectImpl: Any): Int {
        val inetSocketAddress = InetSocketAddress(0)
        val responder = ReflectResponder(clazzInterface, objectImpl)
        val f = NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool())
        val eh = ExecutionHandler(Executors.newCachedThreadPool())
        val server = NettyServer(responder, inetSocketAddress, f, eh)
        server.start()
        return server.port
    }

    private fun getClassImpl(clazz: Class<*>): Class<*> {
        val moyServiceImpl = clazz.getAnnotation(MoyServiceImpl::class.java) as MoyServiceImpl
        return Class.forName(moyServiceImpl.impl)
    }

    private fun createObjectImpl(clazz: Class<*>): Any? {
        var objectImpl: Any? = null

        if (clazz.isInterface) {
//            val clazz_ServiceBuilder = Class.forName("com.grouphigh.ws.client.ServiceBuilder")
//            val serviceBuilder = clazz_ServiceBuilder.getMethod("get").invoke(null)
//            service = clazz_ServiceBuilder.getMethod("createService", Class<*>::class.java).invoke(serviceBuilder, clazz)
        } else {
            // create local via static method
            try {
                objectImpl = clazz.getDeclaredMethod("getOrCreateInstance").invoke(null)
            } catch (e: NoSuchMethodException) {
                // it's OK
            } catch (e: Exception) {
                logger.warn { "could not create local-impl : " + clazz }
            }

            // create via constructors
            if (objectImpl == null) {
                // get constructors and order by parameters count
                val constructors = TreeMap<Int, Constructor<*>>()
                for (constructor in clazz.constructors) {
                    constructors.put(constructor.parameterCount, constructor)
                }

                objectImpl = constructors.asSequence()
                        .map {
                            println("ccc = " + it.key)
                            val parameters = it.value.parameters
                            val params = arrayOfNulls<Any>(parameters.size)
                            for (i in params.indices) {
                                params[i] = createObjectImpl(parameters[i].type)
                            }
                            it.value.newInstance(*params)
                        }.filterNotNull()
                        .first()
            }
        }

        return objectImpl
    }
}