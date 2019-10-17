package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GeolocationResolverTest {

	@Test
	fun canResolveLocation() {
		val tradingPartner = getTradingPartner("Gartenstraße 67", "76135", "Karlsruhe", "DEUTSCHLAND")
		val lookupResult = Geolocation(lat = "49.0049809", lon = "8.3839609")
		val geolocation = runBlocking { tradingPartner.geolocation { _, _, _, _ -> lookupResult } }
		assertThat(geolocation.lat.toDouble(), `is`(closeTo(49.0049809, 0.001)))
		assertThat(geolocation.lon.toDouble(), `is`(closeTo(8.3839609, 0.001)))
	}

	@Test
	fun returnsNullForLatitudeAndLongitudeIfNoAddressIsFound() {
		val tradingPartner = getTradingPartner("invalid", "invalid", "does not exist", "UNITED STATES")
		val lookupResult = Geolocation()
		val geolocation = runBlocking { tradingPartner.geolocation { _, _, _, _ -> lookupResult } }
		assertThat(geolocation.lat, `is`(""))
		assertThat(geolocation.lon, `is`(""))
	}

	private fun getTradingPartner(street: String, zipCode: String, town: String, country: String): TradingPartner {
		val tradingPartner = mock(TradingPartner::class.java)
		`when`(tradingPartner.street).thenReturn(street)
		`when`(tradingPartner.zipCode).thenReturn(zipCode)
		`when`(tradingPartner.town).thenReturn(town)
		val regionCountryEconomicArea = getRegionCountryEconomicArea(country)
		`when`(tradingPartner.stateOfTaxOffice).thenReturn(regionCountryEconomicArea)
		return tradingPartner
	}

	private fun getRegionCountryEconomicArea(country: String): RegionCountryEconomicArea {
		val regionCountryEconomicArea = mock(RegionCountryEconomicArea::class.java)
		`when`(regionCountryEconomicArea.swd).thenReturn(country)
		return regionCountryEconomicArea
	}

}
