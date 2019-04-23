package org.otnip.hbase.marshalling

annotation class ReflectInfo(
        val namespace: String,
        val table: String,
        val family: String,
        val row: String) {
}