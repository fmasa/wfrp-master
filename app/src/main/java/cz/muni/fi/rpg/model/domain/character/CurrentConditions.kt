package cz.muni.fi.rpg.model.domain.character

class CurrentConditions private constructor(private val conditions: Map<Condition, Int>) {
    companion object {
        fun none() = CurrentConditions(emptyMap())
    }

    fun addConditions(vararg conditions: Condition) =
        conditions.fold(this) { acc, condition ->
            val count = (acc.conditions[condition] ?: 0)

            if (! condition.isStackable() && count == 1) {
                acc
            } else {
                withCondition(condition, count + 1)
            }
        }

    fun removeCondition(condition: Condition): CurrentConditions =
        when (val count = conditions[condition] ?: 0) {
            0 -> this
            1 -> CurrentConditions(conditions.filterKeys { it != condition })
            else -> withCondition(condition, count - 1)
        }

    fun toMap() = conditions

    private fun withCondition(condition: Condition, count: Int) = CurrentConditions(
        mapOf(
            *conditions.toList().toTypedArray(),
            condition to count
        )
    )

    override fun equals(other: Any?) = other is CurrentConditions && conditions == other.conditions
    override fun hashCode() = conditions.hashCode()
}