package de.abas.esdk.g30l0

import de.abas.erp.db.DbContext
import de.abas.erp.db.schema.customer.Customer
import de.abas.erp.db.schema.customer.CustomerContact
import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.vendor.Vendor
import de.abas.erp.db.schema.vendor.VendorContact
import de.abas.esdk.dsl.eq
import de.abas.esdk.dsl.or
import de.abas.esdk.dsl.query
import kotlin.reflect.KClass

object TradingPartnerSelector {

	fun selectTradingPartners(ctx: DbContext, swd: String, zipCode: String): List<TradingPartner> {

		fun <T : TradingPartner> selectFromTradingPartner(clazz: Class<T>): List<T> = ctx.query(clazz) {
			or(TradingPartner.META.swd eq swd, TradingPartner.META.zipCode eq zipCode)
		}

		return listOf<KClass<out TradingPartner>>(Customer::class, CustomerContact::class, Vendor::class, VendorContact::class)
				.flatMap { selectFromTradingPartner(it.java) }
	}
}
