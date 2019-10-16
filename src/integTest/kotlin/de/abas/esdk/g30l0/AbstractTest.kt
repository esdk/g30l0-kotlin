package de.abas.esdk.g30l0

import de.abas.erp.db.schema.customer.Customer
import de.abas.erp.db.schema.customer.CustomerContact
import de.abas.erp.db.schema.customer.CustomerContactEditor
import de.abas.erp.db.schema.customer.CustomerEditor
import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.referencetypes.TradingPartnerEditor
import de.abas.erp.db.schema.vendor.Vendor
import de.abas.erp.db.schema.vendor.VendorContact
import de.abas.erp.db.schema.vendor.VendorContactEditor
import de.abas.erp.db.schema.vendor.VendorEditor
import de.abas.esdk.g30l0.AbstractTest.TestingData.*
import de.abas.esdk.test.util.EsdkIntegTest
import de.abas.esdk.test.util.TestData
import org.junit.AfterClass
import org.junit.BeforeClass

abstract class AbstractTest : EsdkIntegTest() {

	companion object {

		@JvmStatic
		@BeforeClass
		fun prepare() {
			createTestData(CustomerEditor::class.java, CUSTOMER)
			createTestData(CustomerContactEditor::class.java, CUSTOMER_CONTACT)
			createTestData(VendorEditor::class.java, VENDOR)
			createTestData(VendorContactEditor::class.java, VENDOR_CONTACT)
			createTestData(CustomerEditor::class.java, INVALID)
		}

		private fun <T : TradingPartnerEditor> createTestData(clazz: Class<T>, testingData: TestingData) {
			testingData.tradingPartner = ctx.newObject(clazz).apply {
				when (this) {
					is CustomerContactEditor -> companyARAP = CUSTOMER.tradingPartner as Customer
					is VendorContactEditor -> companyARAP = VENDOR.tradingPartner as Vendor
				}
				swd = testingData.swd
				zipCode = testingData.zipCode
				town = testingData.town
				commit()
			}
		}

		@JvmStatic
		@AfterClass
		fun cleanup() {
			TestData.deleteData(ctx, Customer::class.java, Customer.META.swd, CUSTOMER.swd)
			TestData.deleteData(ctx, CustomerContact::class.java, CustomerContact.META.swd, CUSTOMER_CONTACT.swd)
			TestData.deleteData(ctx, Vendor::class.java, Vendor.META.swd, VENDOR.swd)
			TestData.deleteData(ctx, VendorContact::class.java, VendorContact.META.swd, VENDOR_CONTACT.swd)
			TestData.deleteData(ctx, Customer::class.java, Customer.META.swd, INVALID.swd)
		}
	}

	enum class TestingData(
		val swd: String,
		val zipCode: String,
		val town: String,
		var tradingPartner: TradingPartner? = null
	) {
		CUSTOMER("G30L0CUS", "67165", "Waldsee"),
		VENDOR("G30L0VEN", "76135", "Karlsruhe"),
		CUSTOMER_CONTACT("G30L0CCO", "67346", "Speyer"),
		VENDOR_CONTACT("G30L0VCO", "76227", "Karlsruhe"),
		INVALID("G30L0INV", "invalid", "invalid")
	}

}
