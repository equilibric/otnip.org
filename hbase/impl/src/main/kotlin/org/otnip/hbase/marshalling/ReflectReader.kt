package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Table
import org.otnip.hbase.marshalling.Utils.encodeBytes
import kotlin.reflect.KClass

class ReflectReader<T>(clazz: KClass<*>, connection: Connection) : AutoCloseable {

    private val decoder = ReflectDecoder<T>(clazz)
    private val table: Table
    private val families = decoder.families

    init {
        table = connection.getTable(decoder.tableName)
    }

    fun read(id: Any, maxAge: Long? = null, datum: T? = null): T? {
        val get = Get(encodeBytes(id))
        if (maxAge != null) {
            get.setTimeStamp(System.currentTimeMillis() - maxAge)
        }
        families.forEach { get.addFamily(it) }

        val result = table.get(get)

        return decoder.decode(result, datum)
    }

    override fun close() {
        table.close()
    }
}