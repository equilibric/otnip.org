package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.Delete
import org.apache.hadoop.hbase.client.Table
import kotlin.reflect.KClass

class ReflectWriter<T>(clazz: KClass<*>, connection: Connection)  : AutoCloseable {

    private val encoder = ReflectEncoder<T>(clazz)
    private val table: Table

    init {
        table = connection.getTable(encoder.tableName)
    }

    fun write(input: T, vararg fields: String) {
        table.put(encoder.encode(input, null, *fields))
    }

    fun deleteRow(input : T) {
        table.delete(Delete(encoder.encodeRow(input)))
    }

    override fun close() {
        table.close()
    }

    //TODO:  delete famlies and columns
}