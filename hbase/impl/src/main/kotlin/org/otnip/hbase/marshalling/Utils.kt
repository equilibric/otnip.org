package org.otnip.hbase.marshalling

import org.apache.hadoop.hbase.util.Bytes
import java.lang.UnsupportedOperationException
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf

val String.bytes : ByteArray
    get() = Bytes.toBytes(this)

object Utils {
    fun encodeBytes(input: Any): ByteArray {
        return when (input) {
            is String -> Bytes.toBytes(input)
            is Int -> Bytes.toBytes(input)
            is Long -> Bytes.toBytes(input)
            is ByteArray -> input
            else -> throw UnsupportedOperationException("type : " + input::class.java)
        }
    }

    fun decodeBytes(input: ByteArray, type: KType): Any {
        return when {
            type.isSupertypeOf(String::class.createType()) -> Bytes.toString(input)
            type.isSupertypeOf(Int::class.createType()) -> Bytes.toInt(input)
            type.isSupertypeOf(Long::class.createType()) -> Bytes.toLong(input)
            type.isSupertypeOf(ByteArray::class.createType()) -> input
            else -> throw UnsupportedOperationException("type : " + input::class.java)
        }
    }
}