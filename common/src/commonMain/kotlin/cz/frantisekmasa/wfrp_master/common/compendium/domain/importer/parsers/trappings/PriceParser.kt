package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.Money

object PriceParser {

    private val PRICE_REGEX = Regex("((\\d+) ?GC)? ?((\\d+|–|-)/(\\d+|–|-))?")
    private val PENNIES_REGEX = Regex("(\\d+)d")

    fun parse(text: String): Result {
        val trimmedValue = text.trim()

        val crownPrice = trimmedValue.toIntOrNull()

        if (crownPrice != null) {
            // In Up in Arms, some items are missing "GC" suffix
            return Amount(Money.crowns(crownPrice))
        }

        val penniesResult = PENNIES_REGEX.matchEntire(trimmedValue)

        if (penniesResult != null) {
            return Amount(Money.pennies(penniesResult.groupValues[1].toInt()))
        }

        if (trimmedValue == "N/A") {
            return Amount(Money.ZERO)
        }

        if (trimmedValue == "–") {
            return Amount(Money.ZERO)
        }

        if (trimmedValue.equals("Varies", ignoreCase = true)) {
            return Varies
        }

        val result = PRICE_REGEX.matchEntire(text.trim()) ?: error("Invalid price $text")

        return Amount(
            Money.sum(
                Money.crowns(result.groupValues[2].toIntOrNull() ?: 0),
                Money.shillings(result.groupValues[4].toIntOrNull() ?: 0),
                Money.pennies(result.groupValues[5].toIntOrNull() ?: 0),
            )
        )
    }

    sealed interface Result
    object Varies : Result
    data class Amount(val money: Money) : Result
}
