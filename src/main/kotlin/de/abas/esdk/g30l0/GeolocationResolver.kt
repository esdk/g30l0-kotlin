package de.abas.esdk.g30l0

import de.abas.erp.db.schema.referencetypes.TradingPartner

interface GeolocationResolver {
	fun resolve(tradingPartner: TradingPartner): Geolocation
}
