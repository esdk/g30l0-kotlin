package de.abas.esdk.g30l0

import de.abas.erp.db.DbContext
import de.abas.erp.db.schema.customer.Customer
import de.abas.erp.db.schema.customer.CustomerContact
import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.vendor.Vendor
import de.abas.erp.db.schema.vendor.VendorContact
import de.abas.erp.db.selection.Conditions
import de.abas.erp.db.selection.SelectionBuilder

class TradingPartnerSelector {

	fun selectTradingPartners(ctx: DbContext, swd: String, zipCode: String): List<TradingPartner> {
		val tradingPartners = mutableListOf<TradingPartner>()
		tradingPartners.addAll(selectFromTradingPartner(Customer::class.java, ctx, swd, zipCode))
		tradingPartners.addAll(selectFromTradingPartner(CustomerContact::class.java, ctx, swd, zipCode))
		tradingPartners.addAll(selectFromTradingPartner(Vendor::class.java, ctx, swd, zipCode))
		tradingPartners.addAll(selectFromTradingPartner(VendorContact::class.java, ctx, swd, zipCode))
		return tradingPartners
	}

	private fun <T : TradingPartner> selectFromTradingPartner(clazz: Class<T>, ctx: DbContext, swd: String, zipCode: String): List<T> = ctx.createQuery(SelectionBuilder.create(clazz)
			.add(Conditions.eq(TradingPartner.META.swd, swd))
			.add(Conditions.eq(TradingPartner.META.zipCode, zipCode))
			.setTermConjunction(SelectionBuilder.Conjunction.OR).build())
			.execute()

}
