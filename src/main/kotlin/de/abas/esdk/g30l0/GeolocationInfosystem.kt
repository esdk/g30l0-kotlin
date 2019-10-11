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
        TradingPartnerSelector
                .selectTradingPartners(ctx, infosystem.customersel, infosystem.zipcodesel)
                .forEach { tradingPartner ->
                    infosystem.table().appendRow().run {
                        customer = tradingPartner
                        zipcode = tradingPartner.zipCode
                        town = tradingPartner.town
                        state = tradingPartner.stateOfTaxOffice
                        tradingPartner.geolocation().let {
                            latitude = it.latitude
                            longitude = it.longitude
                        }
                    }
                }
    }

}
