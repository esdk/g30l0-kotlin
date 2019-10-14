package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import fr.dudie.nominatim.client.JsonNominatimClient
import fr.dudie.nominatim.model.Address
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.Logger
import java.io.IOException

internal object OpenStreetMapGeolocationResolver : GeolocationResolver {
	private val logger: Logger = Logger.getLogger(OpenStreetMapGeolocationResolver::class.java)

	override fun resolve(tradingPartner: TradingPartner): Geolocation {
		val addresses = try {
			resolveFromOpenStreetMaps(tradingPartner)
		} catch (e: IOException) {
			logger.error("Invalid address '${tradingPartner.formattedAddress()}': ${e.message}", e)
			return Geolocation()
		}

		return addresses.firstOrNull()?.run {
			Geolocation(latitude.toString(), longitude.toString())
		} ?: Geolocation().also {
			logger.debug("No matches found for address ${tradingPartner.formattedAddress()}")
		}
	}

	private fun TradingPartner.formattedAddress() = "$street, $zipCode $town, ${stateOfTaxOffice.swd}"

	@Throws(IOException::class)
	private fun resolveFromOpenStreetMaps(tradingPartner: TradingPartner): List<Address> {
		val jsonNominatimClient = JsonNominatimClient(DefaultHttpClient(), "scrumteamesdk@abas.de")
		with(tradingPartner) {
			return jsonNominatimClient.search("$street $zipCode $town ${stateOfTaxOffice.swd}")
		}
	}
}

fun TradingPartner.geolocation() = OpenStreetMapGeolocationResolver.resolve(this)