package org.hyperledger.identus.apollo.utils

/**
 * The _require function is used to import modules in JavaScript.
 *
 * @param name The name of the module to import.
 *
 * @return The imported module.
 */
@Suppress("FunctionName", "UNUSED_PARAMETER")
fun _require(name: String): dynamic = js("require(name)")

/**
 * A variable that represents the global object in JavaScript.
 *
 * The `global` variable is initialized lazily using the `lazy` delegate.
 * It is assigned a dynamic value that depends on the current execution environment.
 *
 * In a Node.js environment, the `global` variable refers to the global object.
 * In a browser environment, the `global` variable refers to the `window` object.
 */
val global: dynamic by lazy {
    js("((typeof global !== 'undefined') ? global : window)")
}

/**
 * Determines if the code is running in a Node.js environment.
 *
 * @return `true` if the code is running in a Node.js environment, `false` otherwise.
 */
val isNode: Boolean by lazy {
    val runtime: String? =
        try {
            // May not be available, but should be preferred
            // method of determining runtime environment.
            js("(globalThis.process.release.name)") as String
        } catch (_: Throwable) {
            null
        }

    when (runtime) {
        null -> {
            js("(typeof global !== 'undefined' && ({}).toString.call(global) == '[object global]')") as Boolean
        }
        "node" -> true
        else -> false
    }
}
