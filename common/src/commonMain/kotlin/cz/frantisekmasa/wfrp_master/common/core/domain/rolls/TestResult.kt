package cz.frantisekmasa.wfrp_master.common.core.domain.rolls

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
data class TestResult(
    val rollValue: Int,
    val testedValue: Int,
) : Parcelable {
    init {
        require(rollValue in 1..100) { "Roll value must be larger than 0 and lower or equal than 100" }
    }

    val isFumble: Boolean
        get() = !isSuccess && rollsDouble()

    val isCritical: Boolean
        get() = isSuccess && rollsDouble()

    private val isSuccess: Boolean
        get() = successLevel > 0 || rollValue <= testedValue

    val successLevel: Int
        get() = when (rollValue) {
            in 1..5 -> 1
            in 96..100 -> -1
            else -> testedValue / 10 - rollValue / 10
        }

    val successLevelText: String
        get() = (if (isSuccess) "+" else "") + successLevel

    val dramaticResult: DramaticResult
        get() {
            val successLevel = successLevel

            return when {
                successLevel == 0 && isSuccess -> DramaticResult.MARGINAL_SUCCESS
                successLevel == 0 && !isSuccess -> DramaticResult.MARGINAL_FAILURE
                else -> DramaticResult.values().first { successLevel in it.successLevelRange }
            }
        }

    override fun toString(): String = (rollValue % 100).toString().padStart(2, '0')

    private fun rollsDouble() = rollValue == 100 || rollValue % 11 == 0 // 100 is rolled as 00, thus special treatment

    enum class DramaticResult(
        val successLevelRange: IntRange,
        override val nameResolver: (strings: Strings) -> String,
    ): NamedEnum {
        ASTOUNDING_SUCCESS(6..Int.MAX_VALUE, { it.tests.results.astoundingSuccess }),
        IMPRESSIVE_SUCCESS(4..5, { it.tests.results.impressiveSuccess }),
        SUCCESS(2..3, { it.tests.results.success }),
        MARGINAL_SUCCESS(0..1, { it.tests.results.marginalSuccess }),
        MARGINAL_FAILURE(-1..0, { it.tests.results.marginalFailure }),
        FAILURE(-3..-2, { it.tests.results.failure }),
        IMPRESSIVE_FAILURE(-5..-4, { it.tests.results.impressiveFailure }),
        ASTOUNDING_FAILURE(Int.MIN_VALUE..-6, { it.tests.results.astoundingFailure }),
    }
}
