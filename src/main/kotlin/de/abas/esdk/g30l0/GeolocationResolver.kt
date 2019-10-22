package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

val log: org.slf4j.Logger = LoggerFactory.getLogger("de.abas.esdk.g30l0")

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
 * A default [HttpClient] is provided, but can be replaced (e. g. for tests).
 */
suspend fun getOpenStreetMapGeolocation(
	street: String,
	zipCode: String,
	town: String,
	country: String,
	httpClient: HttpClient = defaultHttpClient()
): Geolocation {
	val queryString = "$street, $zipCode, $town, $country"
	val email = "scrumteamesdk@abas.de"
	log.debug("Looking up: $queryString")
	httpClient.use {
		val geolocationList: List<Geolocation> = try {
			it.get("https://nominatim.openstreetmap.org/search?q=$queryString&email=$email&format=json&limit=1")
		} catch (e: Exception) {
			log.error("Error while looking up address: ${e.message}", e)
			listOf()
		}
		log.debug("Looked up : $queryString -> $geolocationList")
		return geolocationList.firstOrNull() ?: Geolocation().also {
			log.debug("No matches found for address $queryString")
		}
	}
}

fun defaultHttpClient(httpClientEngine: HttpClientEngine = Apache.create()) = HttpClient(httpClientEngine) {
	install(JsonFeature) {
		serializer = GsonSerializer {
			// .GsonBuilder
			serializeNulls()
		}
	}
	install(Logging) {
		logger = io.ktor.client.features.logging.Logger.DEFAULT
		level = LogLevel.INFO
	}
}
