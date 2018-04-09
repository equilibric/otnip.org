package org.otnip.moy

import org.otnip.moy.providers.ServiceProviderAvro
import mu.KotlinLogging
import java.lang.reflect.Proxy
import java.util.*


/**
 * creates instances of services
 * ServiceLoader -> ClientProvider -> ServiceProvider
 * is singleton
 *
 * @author David Pinto
 */
object ServiceProvider {

    private val logger = KotlinLogging.logger {}
    private val cache = HashMap<String, Any>()

    /* *************************************************************************
     * OBJECT METHODS
     * ************************************************************************/
    fun <T> get(clazz: Class<T>): T {
        var output: T? = null
        synchronized(cache) {
            output = cache.get(clazz.toString()) as T?

            // create new instance
            if (output == null) {
                // create local impl
                //output = create(clazz)
                output = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), ServiceProviderAvro(clazz)) as T

                // add to cache
                cache.put(clazz.toString(), output as Any)
            }
        }
        return output!!
    }

//    private fun <T> create(clazz: Class<T>): T? {
//        var output: T? = null
//        try {
//            val method = .getImpl(clazz).getDeclaredMethod("getOrCreateInstance")
//            output = method.invoke(null) as T
//        } catch (ex: ClassNotFoundException) {
//            // both exceptions are OK
//        } catch (ex: NoSuchMethodException) {
//        } catch (ex: Exception) {
//            logger.warn { "error creating : ${clazz}" }
//        }
//
//        return output
//    }
}