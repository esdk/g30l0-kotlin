package de.abas.esdk.g30l0

import de.abas.erp.db.DbContext
import de.abas.erp.db.FieldValueProvider
import de.abas.erp.db.SelectableRecord
import de.abas.erp.db.field.StringField
import de.abas.erp.db.schema.customer.Customer
import de.abas.erp.db.schema.customer.CustomerContact
import de.abas.erp.db.schema.referencetypes.TradingPartner
import de.abas.erp.db.schema.vendor.Vendor
import de.abas.erp.db.schema.vendor.VendorContact
import de.abas.erp.db.selection.Condition
import de.abas.erp.db.selection.Conditions
import de.abas.erp.db.selection.SelectionBuilder
import kotlin.reflect.KClass

object TradingPartnerSelector {

	private infix fun <O : FieldValueProvider> StringField<O>.eq(value: CharSequence): Condition<O> = Conditions.eq(this, value)

	private fun <T : SelectableRecord> SelectionBuilder<T>.or(vararg conditions: Condition<in T>): SelectionBuilder<T> {
		setTermConjunction(SelectionBuilder.Conjunction.OR)
		addConditions(conditions)
		return this
	}

	private fun <T : SelectableRecord> SelectionBuilder<T>.addConditions(conditions: Array<out Condition<in T>>) {
		conditions.toList().forEach { condition ->
			add(condition)
		}
	}

	private fun <T : SelectableRecord> DbContext.query(clazz: Class<T>, builderAction: SelectionBuilder<T>.() -> SelectionBuilder<T>): List<T> {
		val selection = SelectionBuilder.create(clazz).builderAction().build()
		return createQuery(selection).execute()
	}

	fun selectTradingPartners(ctx: DbContext, swd: String, zipCode: String): List<TradingPartner> {

		fun <T : TradingPartner> selectFromTradingPartner(clazz: Class<T>): List<T> = ctx.query(clazz) {
			or(TradingPartner.META.swd eq swd, TradingPartner.META.zipCode eq zipCode)
		}

		return listOf<KClass<out TradingPartner>>(Customer::class, CustomerContact::class, Vendor::class, VendorContact::class)
				.flatMap { selectFromTradingPartner(it.java) }
	}
}
