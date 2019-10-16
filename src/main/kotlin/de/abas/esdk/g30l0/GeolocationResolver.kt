package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import fr.dudie.nominatim.client.JsonNominatimClient
import fr.dudie.nominatim.model.Address
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.Logger
import java.io.IOException

private val logger: Logger = Logger.getLogger("esdk.g20lO")

typealias TradingPartnerToAddressList = TradingPartner.() -> List<Address>

val openStreetMapGeolocationResolver: TradingPartnerToAddressList =
	{
		val jsonNominatimClient = JsonNominatimClient(DefaultHttpClient(), "scrumteamesdk@abas.de")
		jsonNominatimClient.search("$street $zipCode $town ${stateOfTaxOffice.swd}")
	}

fun TradingPartner.geolocation(resolve: TradingPartnerToAddressList = openStreetMapGeolocationResolver): Geolocation {
	val formattedAddress = "$street, $zipCode $town, ${stateOfTaxOffice.swd}"
	val addresses = try {
		resolve()
	} catch (e: IOException) {
		logger.error("Invalid address '$formattedAddress': ${e.message}", e)
		return Geolocation()
	}
	return addresses.firstOrNull()?.run {
		Geolocation(latitude.toString(), longitude.toString())
	} ?: Geolocation().also {
		logger.debug("No matches found for address $formattedAddress")
	}

}