package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import org.apache.log4j.Logger

private val logger: Logger = Logger.getLogger("de.abas.esdk.g30l0")

/**
 * Suspending function type taking street, zipCode, town, and country and returning a [Geolocation].
 * @see [getOpenStreetMapGeolocation].
 */
private typealias AddressPartsToGeolocation = suspend (String, String, String, String) -> Geolocation

data class Geolocation(val lat: String = "", val lon: String = "")

suspend fun TradingPartner.geolocation(
	resolveGeolocation: AddressPartsToGeolocation =
		{ street: String, zipCode: String, town: String, country: String ->
			getOpenStreetMapGeolocation(
				street,
				zipCode,
				town,
				country
			)
		}
): Geolocation =
	resolveGeolocation(street, zipCode, town, stateOfTaxOffice.swd)

/**
 * Retrieves [Geolocation] for given address parameters in OpenStreetMap.
 *
 * Only the one result is returned.
 * If location lookup fails a [Geolocation] with empty coordinates is returned.
 */
suspend fun getOpenStreetMapGeolocation(street: String, zipCode: String, town: String, country: String): Geolocation {
	val queryString = "$street, $zipCode, $town, $country"
	val email = "scrumteamesdk@abas.de"
	logger.debug("Looking up: $queryString")
	defaultHttpClient().use {
		val geolocationList: List<Geolocation> = try {
			it.get("https://nominatim.openstreetmap.org/search?q=$queryString&email=$email&format=json&limit=1")
		} catch (e: Exception) {
			logger.error("Error while looking up address: ${e.message}", e)
			listOf()
		}
		logger.debug("Looked up : $queryString -> $geolocationList")
		return geolocationList.firstOrNull() ?: Geolocation().also {
			logger.debug("No matches found for address $queryString")
		}
	}
}

private fun defaultHttpClient(): HttpClient {
	return HttpClient(Apache) {
		install(JsonFeature) {
			serializer = GsonSerializer {
				serializeNulls()
			}
		}
		// Nominatim requires to set a meaningful User-Agent header.
		install(UserAgent) {
			agent = "ESDK Demo App"
		}
	}
}
