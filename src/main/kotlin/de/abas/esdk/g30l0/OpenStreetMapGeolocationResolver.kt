package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import fr.dudie.nominatim.client.JsonNominatimClient
import fr.dudie.nominatim.model.Address
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.Logger
import java.io.IOException


class OpenStreetMapGeolocationResolver : GeolocationResolver {
	private val logger: Logger = Logger.getLogger(OpenStreetMapGeolocationResolver::class.java)

	override fun resolve(tradingPartner: TradingPartner): Geolocation {
		val geolocation = Geolocation()
		try {
			val addresses = resolveFromOpenStreetMaps(tradingPartner)
			if (addresses.isEmpty()) {
				logger.debug("No matches found for address ${getFormattedAddress(tradingPartner)}")
			} else {
				geolocation.setLatitude(addresses[0].latitude)
				geolocation.setLongitude(addresses[0].longitude)
			}
		} catch (e: IOException) {
			logger.error("Invalid address '${getFormattedAddress(tradingPartner)}': ${e.message}", e)
		}
		return geolocation
	}

	private fun getFormattedAddress(tradingPartner: TradingPartner) = "${tradingPartner.street}, ${tradingPartner.zipCode} ${tradingPartner.town}, ${tradingPartner.stateOfTaxOffice.swd}"

	@Throws(IOException::class)
	private fun resolveFromOpenStreetMaps(tradingPartner: TradingPartner): List<Address> {
		val jsonNominatimClient = JsonNominatimClient(DefaultHttpClient(), "scrumteamesdk@abas.de")
		return jsonNominatimClient.search("${tradingPartner.street} ${tradingPartner.zipCode} ${tradingPartner.town} ${tradingPartner.stateOfTaxOffice.swd}")
	}

}
