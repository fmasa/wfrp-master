package cz.frantisekmasa.wfrp_master.common

import kotlin.math.min

data class Money(private val pennies: Int) : Comparable<Money> {
    companion object {
        private const val SHILLINGS_IN_CROWNS = 20
        private const val PENNIES_IN_SHILLING = 12

        fun zero() = Money(0)

        fun pennies(pennies: Int) = Money(pennies)
        fun shillings(shillings: Int) = Money(shillings * PENNIES_IN_SHILLING)
        fun crowns(crowns: Int) = shillings(crowns * SHILLINGS_IN_CROWNS)
    }

    init {
        require(pennies >= 0) { "Amount of money cannot be negative" }
    }

    fun getPennies() = pennies % PENNIES_IN_SHILLING

    fun getShillings() = (pennies / PENNIES_IN_SHILLING) % SHILLINGS_IN_CROWNS

    fun getCrowns() = pennies / PENNIES_IN_SHILLING / SHILLINGS_IN_CROWNS

    operator fun plus(other: Money) = Money(
        min(pennies.toLong() + other.pennies.toLong(), Int.MAX_VALUE.toLong()).toInt()
    )

    operator fun minus(other: Money) = Money(pennies - other.pennies)

    override fun compareTo(other: Money) = pennies.compareTo(other.pennies)
}
