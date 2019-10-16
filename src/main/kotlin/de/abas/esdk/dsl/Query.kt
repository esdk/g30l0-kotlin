/**
 * Skeleton of a DSL to build selections and execute queries.
 *
 * This is tailored to be used by TradingPartnerSelector but it could be developed into a universal DSL.
 */
package de.abas.esdk.dsl

import de.abas.erp.db.DbContext
import de.abas.erp.db.FieldValueProvider
import de.abas.erp.db.SelectableRecord
import de.abas.erp.db.field.StringField
import de.abas.erp.db.selection.Condition
import de.abas.erp.db.selection.Conditions
import de.abas.erp.db.selection.SelectionBuilder

/**
 * The runtime class instance of the objects which should be selected.
 * Must be a subtype of [SelectableRecord].
 */
typealias Type<T> = Class<T>

/**
 * Creates a [SelectionBuilder] for [clazz], builds the selection using the statements of [builderAction],
 * creates a [Query][de.abas.erp.db.Query] and finally executes it.
 *
 * @sample de.abas.esdk.g30l0.TradingPartnerSelector.selectTradingPartners
 */
fun <T : SelectableRecord> DbContext.query(
	clazz: Type<T>,
	builderAction: SelectionBuilder<T>.() -> SelectionBuilder<T>
): List<T> {
	val selection = SelectionBuilder.create(clazz).builderAction().build()
	return createQuery(selection).execute()
}

/**
 * Sets [SelectionBuilder.setTermConjunction] to [OR][SelectionBuilder.Conjunction.OR] and adds all [conditions].
 */
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

/**
 * Applies [Conditions.eq].
 */
infix fun <O : FieldValueProvider> StringField<O>.eq(value: CharSequence): Condition<O> = Conditions.eq(this, value)
