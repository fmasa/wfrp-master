package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import kotlin.random.Random

class FixedRandom(
    private val expectedRange: IntRange,
    private val returnedValue: Int
) : Random() {
    override fun nextInt(from: Int, until: Int): Int {
        require(from == expectedRange.first && until == expectedRange.last + 1) {
            "Expected " +
                    "nextInt(${expectedRange.first}, ${expectedRange.last + 1}), " +
                    "nextInt($from, $until) called."
        }

        return returnedValue
    }

    override fun nextBits(bitCount: Int) = error("Unexpected random call")
}