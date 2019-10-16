package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import fr.dudie.nominatim.client.JsonNominatimClient
import fr.dudie.nominatim.model.Address
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.Logger
import java.io.IOException

private val logger: Logger = Logger.getLogger("esdk.g20lO")

typealias TradingPartnerToAddressList = TradingPartner.() -> List<Address>

fun TradingPartner.geolocation(resolveAddressList: TradingPartnerToAddressList = openStreetMapGeolocationResolver): Geolocation {
	val addresses = resolveAddressList()
	return addresses.firstOrNull()?.run {
		Geolocation(latitude.toString(), longitude.toString())
	} ?: Geolocation().also {
		logger.debug("No matches found for address ${formattedAddress()}")
	}
}

val openStreetMapGeolocationResolver: TradingPartnerToAddressList = {
	try {
		JsonNominatimClient(DefaultHttpClient(), "scrumteamesdk@abas.de")
			.search("$street $zipCode $town ${stateOfTaxOffice.swd}")
	} catch (e: IOException) {
		logger.error("Invalid address '${formattedAddress()}': ${e.message}", e)
		listOf()
	}
}

private fun TradingPartner.formattedAddress() = "$street, $zipCode $town, ${stateOfTaxOffice.swd}"
