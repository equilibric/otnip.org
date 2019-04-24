package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.otnip.hbase.marshalling.Utils.encodeBytes
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class ReflectEncoder<T>(clazz: kotlin.reflect.KClass<*>) {

    val tableName: TableName
    val family: ByteArray

    private val fields = mutableMapOf<String, KProperty1<*, *>>()
    private val rowField: KProperty1<*, *>

    init {
        val info = clazz.findAnnotation<ReflectInfo>()!!
        this.tableName = TableName.valueOf(info.namespace, info.table)
        this.family = Bytes.toBytes(info.family)
        this.rowField = clazz.declaredMemberProperties.find { it.name == info.row }!!
        clazz.declaredMemberProperties.forEach {
            if (it.name != info.row) {
                fields[it.name] = it
            }
        }
    }

    fun encode(input: T, put: Put? = null, vararg fields: String): Put {
        val output = put ?: Put(encodeRow(input))

        val fieldsSequence = if (fields.isNotEmpty()) fields.asSequence() else this.fields.keys.asSequence()
        fieldsSequence.forEach { fieldName ->
            val value = this.fields[fieldName]!!.getter.call(input)
            if (value != null) {
                output.addColumn(family, Bytes.toBytes(fieldName), encodeBytes(value))
            }
        }

        return output
    }

    fun encodeRow(input: T): ByteArray {
        return encodeBytes(rowField.getter.call(input)!!)
    }

}