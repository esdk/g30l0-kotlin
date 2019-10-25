package de.abas.esdk.g30l0

import de.abas.erp.db.EditorObject

/**
 * Executes the given [block] function on this [EditorObject] and then closes it down correctly whether an exception
 * is thrown or not.
 *
 * @sample de.abas.esdk.g30l0.CustomerCalculateGeolocationTest.canFillGeolocation
 */
fun EditorObject.use(block: (EditorObject) -> Unit) {
	var exception: Throwable? = null
	try {
		return block(this)
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		when {
			exception == null -> abort()
			else ->
				try {
					abort()
				} catch (abortException: Throwable) {
					// ignored here
				}
		}
	}
}
