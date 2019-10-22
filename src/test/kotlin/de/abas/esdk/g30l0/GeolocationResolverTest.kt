package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class GeolocationResolverTest {

	@Test
	fun `geolocation is a function of TradingPartner`() {
		val tradingPartner = getTradingPartner("Gartenstraße 67", "76135", "Karlsruhe", "DEUTSCHLAND")
		val lookupResult = Geolocation(lat = "49.0049809", lon = "8.3839609")
		val geolocation = runBlocking {
			tradingPartner.geolocation { street, _, _, _ ->
				assertThat("Parameter is forwarded to resolver function", street, `is`("Gartenstraße 67"))
				lookupResult
			}
		}
		assertThat(geolocation.lat.toDouble(), `is`(closeTo(49.0049809, 0.001)))
		assertThat(geolocation.lon.toDouble(), `is`(closeTo(8.3839609, 0.001)))
	}

	@Suppress("SameParameterValue")
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

	@Test
	fun `when OpenStreetMap returns data, an Geolocation object is returned`() {
		val geolocation = runBlocking {
			log.debug("Using mock HTTP client...")
			getOpenStreetMapGeolocation(
				"Gartenstraße 67",
				"76135",
				"Karlsruhe",
				"DEUTSCHLAND",
				defaultMockHttpClient()
			)
		}
		assertThat(geolocation.lat.toDouble(), `is`(closeTo(49.0049809, 0.001)))
		assertThat(geolocation.lon.toDouble(), `is`(closeTo(8.3839609, 0.001)))
	}

	@Test
	fun `if no address is found, an empty Geolocation object is returned`() {
		val geolocation = runBlocking {
			log.debug("Using mock HTTP client...")
			getOpenStreetMapGeolocation(
				"invalid",
				"invalid",
				"does not exist",
				"UNITED STATES",
				defaultMockHttpClient()
			)
		}
		assertThat(geolocation.lat, `is`(""))
		assertThat(geolocation.lon, `is`(""))
	}

	private fun defaultMockHttpClient(): HttpClient = defaultHttpClient(
		MockEngine(MockEngineConfig().also {
			it.addHandler { request ->
				when {
					request.url.parameters.contains("q", "Gartenstraße 67, 76135, Karlsruhe, DEUTSCHLAND") -> respond(
						ByteReadChannel(
							"""
							[
							  {
								"place_id": 62839345,
								"licence": "Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright",
								"osm_type": "node",
								"osm_id": 5209398233,
								"boundingbox": [
								  "49.00404",
								  "49.00414",
								  "8.3848452",
								  "8.3849452"
								],
								"lat": "49.00409",
								"lon": "8.3848952",
								"display_name": "synyx, 67, Gartenstraße, Beiertheimer Feld, Südweststadt, Karlsruhe, Regierungsbezirk Karlsruhe, Baden-Württemberg, 76135, Deutschland",
								"class": "office",
								"type": "company",
								"importance": 0.551
							  }
							]
						""".trimIndent().toByteArray(Charsets.UTF_8)
						),
						HttpStatusCode.OK,
						headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
					)
					request.url.parameters.contains("q", "invalid, invalid, does not exist, UNITED STATES") -> respond(
						ByteReadChannel("[]".toByteArray(Charsets.UTF_8)),
						HttpStatusCode.OK,
						headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
					)
					else -> error("Unhandled")
				}
			}
		})
	)

}
