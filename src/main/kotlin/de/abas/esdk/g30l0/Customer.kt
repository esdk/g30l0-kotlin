package de.abas.esdk.g30l0

import de.abas.erp.axi2.EventHandlerRunner
import de.abas.erp.axi2.annotation.ButtonEventHandler
import de.abas.erp.axi2.annotation.EventHandler
import de.abas.erp.axi2.type.ButtonEventType
import de.abas.erp.db.schema.customer.CustomerEditor
import de.abas.erp.jfop.rt.api.annotation.RunFopWith

@EventHandler(head = CustomerEditor::class)
@RunFopWith(EventHandlerRunner::class)
class Customer {

    @ButtonEventHandler(field = "yg30l0calcgeoloc", type = ButtonEventType.AFTER)
    fun calcGeolocAfter(customerEditor: CustomerEditor) {
        OpenStreetMapGeolocationResolver().resolve(customerEditor).let {
            customerEditor.latitude = it.latitude.toBigDecimal()
            customerEditor.longitude = it.longitude.toBigDecimal()
        }
    }

}
