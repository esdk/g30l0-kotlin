package de.abas.esdk.g30l0

import de.abas.erp.common.type.IdImpl
import de.abas.erp.db.EditorAction
import de.abas.erp.db.schema.customer.Customer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.Test

class CustomerCalculateGeolocationTest : AbstractTest() {

	@Test
	fun canFillGeolocation() {
		val customer = ctx.load(Customer::class.java, IdImpl.valueOf(TestingData.CUSTOMER.tradingPartner!!.id().toString()))
		val customerEditor = customer.createEditor()
		try {
			customerEditor.open(EditorAction.UPDATE)
			customerEditor.invokeButton("yg30l0calcgeoloc")
			assertThat(customerEditor.latitude.toDouble(), `is`(closeTo(49.3953008, 0.1)))
			assertThat(customerEditor.longitude.toDouble(), `is`(closeTo(8.440276, 0.1)))
		} finally {
			if (customerEditor.active()) {
				customerEditor.abort()
			}
		}
	}

}
