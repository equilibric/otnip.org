package org.otnip.moy.core

import java.util.jar.Manifest

/**
 * Helps access build information relevant to moy.
 */
object BuildInfo {

    private val KEY_HASH = "moy-hash"
    private val KEY_TIMESTAMP = "moy-timestamp"

    val timestamp: Long
        get() {
            var output = 0L
            BuildInfo::class.java.classLoader.getResourceAsStream("META-INF/MANIFEST.MF").use { inputStream ->
                if (inputStream != null) {
                    val attributes = Manifest(inputStream).mainAttributes
                    if (KEY_TIMESTAMP in attributes) {
                        output = attributes.getValue(KEY_TIMESTAMP).toLongOrNull() ?: 0L
                    }
                }
            }
            return output
        }


    val hash: String
        get() {
            var output = ""
            BuildInfo::class.java.classLoader.getResourceAsStream("META-INF/MANIFEST.MF").use { inputStream ->
                if (inputStream != null) {
                    val attributes = Manifest(inputStream).mainAttributes
                    if (KEY_HASH in attributes) {
                        output = attributes.getValue(KEY_HASH)
                    }
                }
            }
            return output
        }
}