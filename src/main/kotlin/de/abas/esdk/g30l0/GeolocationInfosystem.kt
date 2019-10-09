package de.abas.esdk.g30l0

import de.abas.erp.axi2.EventHandlerRunner
import de.abas.erp.axi2.annotation.ButtonEventHandler
import de.abas.erp.axi2.annotation.EventHandler
import de.abas.erp.axi2.type.ButtonEventType
import de.abas.erp.db.DbContext
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation
import de.abas.erp.jfop.rt.api.annotation.RunFopWith

@EventHandler(head = GeoLocation::class, row = GeoLocation.Row::class)
@RunFopWith(EventHandlerRunner::class)
class GeolocationInfosystem {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	fun startAfter(ctx: DbContext, infosystem: GeoLocation) {
		TradingPartnerSelector().selectTradingPartners(ctx, infosystem.customersel, infosystem.zipcodesel).forEach { tradingPartner ->
			val row = infosystem.table().appendRow()
			row.customer = tradingPartner
			row.zipcode = tradingPartner.zipCode
			row.town = tradingPartner.town
			row.state = tradingPartner.stateOfTaxOffice
			val geolocation = OpenStreetMapGeolocationResolver().resolve(tradingPartner)
			row.latitude = geolocation.latitude
			row.longitude = geolocation.longitude
		}
	}

}
