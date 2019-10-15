/*
 * Skeleton of a DSL to build selections and execute queries.
 *
 * This is tailored to be used by [TradingPartnerSelector] but it could be developed into a universal DSL.
 */
package de.abas.esdk.dsl

import de.abas.erp.db.DbContext
import de.abas.erp.db.FieldValueProvider
import de.abas.erp.db.SelectableRecord
import de.abas.erp.db.field.StringField
import de.abas.erp.db.selection.Condition
import de.abas.erp.db.selection.Conditions
import de.abas.erp.db.selection.SelectionBuilder


fun <T : SelectableRecord> DbContext.query(
	clazz: Class<T>,
	builderAction: SelectionBuilder<T>.() -> SelectionBuilder<T>
): List<T> {
	val selection = SelectionBuilder.create(clazz).builderAction().build()
	return createQuery(selection).execute()
}

infix fun <O : FieldValueProvider> StringField<O>.eq(value: CharSequence): Condition<O> = Conditions.eq(this, value)

fun <T : SelectableRecord> SelectionBuilder<T>.or(vararg conditions: Condition<in T>): SelectionBuilder<T> {
	setTermConjunction(SelectionBuilder.Conjunction.OR)
	addConditions(conditions)
	return this
}

private fun <T : SelectableRecord> SelectionBuilder<T>.addConditions(conditions: Array<out Condition<in T>>) {
	conditions.toList().forEach { condition ->
		add(condition)
	}
}