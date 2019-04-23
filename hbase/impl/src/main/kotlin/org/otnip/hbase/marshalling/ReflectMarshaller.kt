package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.client.Connection
import java.io.Closeable
import kotlin.reflect.KClass

class ReflectMarshaller<T>(clazz : KClass<*>, val connection : Connection) : Closeable {

    val encoder = ReflectEncoder<T>(clazz)
    val decoder = ReflectDecoder<T>(clazz)
    val reader = ReflectReader<T>(clazz, connection)
    val writer = ReflectWriter<T>(clazz, connection)

    override fun close() {
        reader.close()
        writer.close()
    }
}