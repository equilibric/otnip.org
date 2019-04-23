package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.otnip.hbase.marshalling.Utils.decodeBytes
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class ReflectDecoder<T>(clazz: kotlin.reflect.KClass<*>) {

    val tableName: TableName
    val families: List<ByteArray>
        get() = listOf(family)


    private val family: ByteArray
    private val fields = mutableMapOf<String, KMutableProperty1<*, *>>()
    private val rowField: KMutableProperty1<*, *>
    private val constructor: KFunction<T>

    init {
        val info = clazz.findAnnotation<ReflectInfo>()!!
        this.constructor = clazz.constructors.find { it.parameters.isEmpty() } as KFunction<T>
        this.tableName = TableName.valueOf(info.namespace, info.table)
        this.family = Bytes.toBytes(info.family)
        this.rowField = clazz.declaredMemberProperties.find { it.name == info.row } as KMutableProperty1<*, *>
        clazz.declaredMemberProperties.forEach {
            if (it.name != info.row) {
                fields[it.name] = it as KMutableProperty1<*, *>
            }
        }
    }

    fun decode(result: Result, datum: Any? = null): T? {
        return if (result.isEmpty) {
            null
        } else {
            val output = datum ?: constructor.call()
            rowField.setter.call(output, decodeBytes(result.row, rowField.returnType))
            fields.values.forEach {
                val value = result.getValue(family, Bytes.toBytes(it.name))
                if (value != null) {
                    it.setter.call(output, decodeBytes(value, it.returnType))
                }
            }
            output as T
        }
    }
}