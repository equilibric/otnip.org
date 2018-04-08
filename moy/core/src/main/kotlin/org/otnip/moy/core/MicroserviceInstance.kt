package org.otnip.moy.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.charset.Charset

@JsonIgnoreProperties(value = arrayOf("json", "bytes" ))
data class MicroserviceInstance(
        val path: String,
        val host: String,
        val port: Int,
        val timestamp: Long,
        val hash: String) {

    val json: String
        get() {
            println(this)
            println(jacksonObjectMapper().writeValueAsString(this))
            return jacksonObjectMapper().writeValueAsString(this)
        }

    val bytes: ByteArray
        get() {
            return json.toByteArray(Charset.forName("utf-8"))
        }
}