package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
@Immutable
@Parcelize
data class Money(private val pennies: Int) : Parcelable, Comparable<Money> {
    companion object {
        private const val SHILLINGS_IN_CROWNS = 20
        private const val PENNIES_IN_SHILLING = 12

        val ZERO = Money(0)

        fun pennies(pennies: Int) = Money(pennies)
        fun shillings(shillings: Int) = Money(shillings * PENNIES_IN_SHILLING)
        fun crowns(crowns: Int) = shillings(crowns * SHILLINGS_IN_CROWNS)

        fun sum(a: Money, vararg addends: Money): Money =
            addends.fold(a) { x, y -> x + y }
    }

    init {
        require(pennies >= 0) { "Amount of money cannot be negative" }
    }

    fun getPennies() = pennies % PENNIES_IN_SHILLING

    fun getShillings() = (pennies / PENNIES_IN_SHILLING) % SHILLINGS_IN_CROWNS

    fun getCrowns() = pennies / PENNIES_IN_SHILLING / SHILLINGS_IN_CROWNS

    fun isZero(): Boolean = pennies == 0

    operator fun plus(other: Money) = Money(
        min(pennies.toLong() + other.pennies.toLong(), Int.MAX_VALUE.toLong()).toInt()
    )

    operator fun minus(other: Money) = Money(pennies - other.pennies)

    override fun compareTo(other: Money) = pennies.compareTo(other.pennies)
}
