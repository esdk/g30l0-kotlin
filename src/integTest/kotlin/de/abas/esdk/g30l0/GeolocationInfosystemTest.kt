package de.abas.esdk.g30l0

import de.abas.erp.db.infosystem.custom.ow1.GeoLocation
import de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER
import de.abas.esdk.g30l0.AbstractTest.TestingData.VENDOR
import de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER_CONTACT
import de.abas.esdk.g30l0.AbstractTest.TestingData.INVALID
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.After
import org.junit.Test

class GeolocationInfosystemTest : AbstractTest() {

	val infosystem = ctx.openInfosystem(GeoLocation::class.java)

	@Test
	fun canDisplayCustomerInfo() {
		infosystem.customersel = CUSTOMER.swd
		infosystem.invokeStart()

		assertInfosystemTableContains(CUSTOMER)
	}

	@Test
	fun canDisplayVendorInfo() {
		infosystem.customersel = VENDOR.swd
		infosystem.invokeStart()

		assertInfosystemTableContains(VENDOR)
	}

	@Test
	fun canDisplayCustomerContactInfo() {
		infosystem.customersel = CUSTOMER_CONTACT.swd
		infosystem.invokeStart()

		assertInfosystemTableContains(CUSTOMER_CONTACT)
	}

	@Test
	fun canSelectBasedOnZipCode() {
		infosystem.zipcodesel = CUSTOMER.zipCode
		infosystem.invokeStart()

		assertInfosystemTableContains(CUSTOMER)
	}

	@Test
	fun canSelectBasedOnZipCodeAndSwd() {
		infosystem.customersel = VENDOR.swd
		infosystem.zipcodesel = CUSTOMER.zipCode
		infosystem.invokeStart()

		assertInfosystemTableContains(CUSTOMER, VENDOR, expectedRowCount = 2)
	}

	@Test
	fun canDisplayGeolocation() {
		infosystem.customersel = CUSTOMER.swd
		infosystem.invokeStart()

		assertThat(infosystem.table().getRow(1).customer.swd, `is`(CUSTOMER.swd))
		assertThat(infosystem.table().getRow(1).latitude.toDouble(), `is`(closeTo(49.3953008, 0.1)))
		assertThat(infosystem.table().getRow(1).longitude.toDouble(), `is`(closeTo(8.440276, 0.1)))
	}

	@Test
	fun displaysEmptyStringForInvalidAddress() {
		infosystem.customersel = INVALID.swd
		infosystem.invokeStart()

		assertThat(infosystem.table().getRow(1).customer.swd, `is`(INVALID.swd))
		assertThat(infosystem.table().getRow(1).latitude, `is`(""))
		assertThat(infosystem.table().getRow(1).longitude, `is`(""))
	}

	private fun assertInfosystemTableContains(vararg testingData: TestingData, expectedRowCount: Int = 1) {
		assertThat(infosystem.table().rowCount, `is`(expectedRowCount))
		testingData.forEachIndexed { index, testData ->
			val rowNo = index + 1
			assertThat(infosystem.table().getRow(rowNo).customer, `is`(notNullValue()))
			assertThat(infosystem.table().getRow(rowNo).customer.swd, `is`(testData.swd))
			assertThat(infosystem.table().getRow(rowNo).zipcode, `is`(testData.zipCode))
			assertThat(infosystem.table().getRow(rowNo).town, `is`(testData.town))
		}
	}

	@After
	fun tidyUp() {
		infosystem.abort()
	}

}
