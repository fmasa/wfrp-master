package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Immutable
@Parcelize
class CurrentConditions private constructor(
    private val conditions: Map<Condition, Int>
) : Parcelable {
    companion object {
        fun none() = CurrentConditions(emptyMap())
    }

    @Stable
    fun areEmpty(): Boolean = conditions.isEmpty()

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
    fun count(condition: Condition) = conditions.getOrElse(condition, { 0 })

    @Stable
    fun toMap() = conditions

    @Stable
    fun toList(): List<Pair<Condition, Int>> {
        return conditions.asSequence()
            .sortedBy { it.key }
            .map { it.toPair() }
            .toList()
    }

    private fun withCondition(condition: Condition, count: Int) = CurrentConditions(
        mapOf(
            *conditions.toList().toTypedArray(),
            condition to count
        )
    )

    override fun equals(other: Any?) = other is CurrentConditions && conditions == other.conditions
    override fun hashCode() = conditions.hashCode()
}
