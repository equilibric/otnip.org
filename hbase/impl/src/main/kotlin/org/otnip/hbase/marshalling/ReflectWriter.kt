package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.Delete
import org.apache.hadoop.hbase.client.Table
import kotlin.reflect.KClass

class ReflectWriter<T>(clazz: KClass<*>, connection: Connection) : AutoCloseable {

    private val encoder = ReflectEncoder<T>(clazz)
    private val table: Table

    init {
        table = connection.getTable(encoder.tableName)
    }

    fun write(input: T, vararg fields: String) {
        table.put(encoder.encode(input, null, *fields))
    }

    fun deleteRow(input: T) {
        table.delete(Delete(encoder.encodeRow(input)))
    }

    fun deleteFamily(input: T) {
        val delete = Delete(encoder.encodeRow(input))
        delete.addFamily(encoder.family)
        table.delete(delete)
    }

    fun deleteColumns(input: T, vararg fields: String) {
        val delete = Delete(encoder.encodeRow(input))
        fields.forEach {
            delete.addColumn(encoder.family, it.toByteArray(Charsets.UTF_8))
        }
        table.delete(delete)
    }

    override fun close() {
        table.close()
    }

    //TODO:  delete famlies and columns
}