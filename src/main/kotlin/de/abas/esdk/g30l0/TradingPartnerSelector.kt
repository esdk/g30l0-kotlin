package de.abas.esdk.g30l0

import de.abas.erp.db.DbContext
import de.abas.erp.db.schema.customer.Customer
import de.abas.erp.db.schema.customer.CustomerContact
import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.vendor.Vendor
import de.abas.erp.db.schema.vendor.VendorContact
import de.abas.erp.db.selection.Conditions
import de.abas.erp.db.selection.SelectionBuilder
import kotlin.reflect.KClass

object TradingPartnerSelector {

	fun selectTradingPartners(ctx: DbContext, swd: String, zipCode: String): List<TradingPartner> {

		fun <T : TradingPartner> selectFromTradingPartner(clazz: Class<T>): List<T> =
				ctx.createQuery(SelectionBuilder.create(clazz)
						.add(Conditions.eq(TradingPartner.META.swd, swd))
						.add(Conditions.eq(TradingPartner.META.zipCode, zipCode))
						.setTermConjunction(SelectionBuilder.Conjunction.OR)
						.build())
						.execute()

		return listOf<KClass<out TradingPartner>>(Customer::class, CustomerContact::class, Vendor::class, VendorContact::class)
				.flatMap { selectFromTradingPartner(it.java) }
	}
}
