package de.abas.esdk.g30l0

import de.abas.erp.axi2.EventHandlerRunner
import de.abas.erp.axi2.annotation.ButtonEventHandler
import de.abas.erp.axi2.annotation.EventHandler
import de.abas.erp.axi2.type.ButtonEventType
import de.abas.erp.db.schema.customer.CustomerEditor
import de.abas.erp.jfop.rt.api.annotation.RunFopWith
import kotlinx.coroutines.runBlocking

@EventHandler(head = CustomerEditor::class)
@RunFopWith(EventHandlerRunner::class)
class Customer {

	@ButtonEventHandler(field = "yg30l0calcgeoloc", type = ButtonEventType.AFTER)
	fun CustomerEditor.calcGeolocAfter() {
		runBlocking {
			geolocation().let {
				latitude = it.lat.toBigDecimal()
				longitude = it.lon.toBigDecimal()
			}
		}
	}

}
