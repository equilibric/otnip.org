package org.otnip.moy.core

private const val KEY_CONTEXT = "moy.context"

/**
 * Gets/Sets the current context.
 */
object Context {

    val isSet: Boolean
        get() {
            return (System.getProperty(KEY_CONTEXT) ?: "").isNotBlank()
        }

    var context: String
        get() {
            return System.getProperty(KEY_CONTEXT, System.getProperty("user.name"))
        }
        set(context) {
            System.setProperty(KEY_CONTEXT, context)
        }
}