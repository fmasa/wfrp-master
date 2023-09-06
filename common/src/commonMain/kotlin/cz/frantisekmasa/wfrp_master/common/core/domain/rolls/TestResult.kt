package cz.frantisekmasa.wfrp_master.common.core.domain.rolls

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
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

    @Immutable
    enum class DramaticResult(
        val successLevelRange: IntRange,
        override val translatableName: StringResource,
    ) : NamedEnum {
        ASTOUNDING_SUCCESS(6..Int.MAX_VALUE, Str.tests_results_astounding_success),
        IMPRESSIVE_SUCCESS(4..5, Str.tests_results_impressive_success),
        SUCCESS(2..3, Str.tests_results_success),
        MARGINAL_SUCCESS(0..1, Str.tests_results_marginal_success),
        MARGINAL_FAILURE(-1..0, Str.tests_results_marginal_failure),
        FAILURE(-3..-2, Str.tests_results_failure),
        IMPRESSIVE_FAILURE(-5..-4, Str.tests_results_impressive_failure),
        ASTOUNDING_FAILURE(Int.MIN_VALUE..-6, Str.tests_results_astounding_failure),
    }
}
