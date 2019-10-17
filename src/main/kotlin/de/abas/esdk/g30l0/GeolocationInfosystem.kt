package de.abas.esdk.g30l0

import de.abas.erp.axi2.EventHandlerRunner
import de.abas.erp.axi2.annotation.ButtonEventHandler
import de.abas.erp.axi2.annotation.EventHandler
import de.abas.erp.axi2.type.ButtonEventType
import de.abas.erp.db.DbContext
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation
import de.abas.erp.jfop.rt.api.annotation.RunFopWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@EventHandler(head = GeoLocation::class, row = GeoLocation.Row::class)
@RunFopWith(EventHandlerRunner::class)
class GeolocationInfosystem {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	fun startAfter(ctx: DbContext, infosystem: GeoLocation) {
		runBlocking {
			val request = launch { TradingPartnerSelector
				.selectTradingPartners(ctx, infosystem.customersel, infosystem.zipcodesel)
				.forEach { tradingPartner ->
					launch {
						infosystem.table().appendRow().run {
							customer = tradingPartner
							zipcode = tradingPartner.zipCode
							town = tradingPartner.town
							state = tradingPartner.stateOfTaxOffice
							tradingPartner.geolocation().let {
								latitude = it.lat
								longitude = it.lon
							}
						}
					}
					//nominatim requires a maximum of 1 request per second.
					//By pausing here, we ensure that we send one request per second,
					//regardless of the execution time of each query.
					delay(1000)
				}
			}
			request.join()
		}
	}

}
