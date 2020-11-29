package cz.frantisekmasa.wfrp_master.core.domain.rolls

data class TestResult(
    val rollValue: Int,
    val testedValue: Int,
) {
    init {
        require(rollValue in 1..100) { "Roll value must be larger than 0 and lower or equal than 100" }
    }

    val isSuccess: Boolean = successLevelNumber() > 0 || rollValue <= testedValue

    val successLevel: String
        get() = (if (isSuccess) "+" else "") + successLevelNumber()

    val dramaticResult: DramaticResult
        get() {
            val successLevel = successLevelNumber()

            return when {
                successLevel == 0 && isSuccess -> DramaticResult.MARGINAL_SUCCESS
                successLevel == 0 && !isSuccess -> DramaticResult.MARGINAL_FAILURE
                else -> DramaticResult.values().first { successLevel in it.successLevelRange }
            }
        }

    override fun toString(): String = (rollValue % 100).toString().padStart(2, '0')

    private fun successLevelNumber() = when (rollValue) {
        in 1..5 -> 1
        in 96..100 -> -1
        else -> testedValue / 10 - rollValue / 10
    }

    enum class DramaticResult(val successLevelRange: IntRange) {
        ASTOUNDING_SUCCESS(6..Int.MAX_VALUE),
        IMPRESSIVE_SUCCESS(4..5),
        SUCCESS(2..3),
        MARGINAL_SUCCESS(0..1),
        MARGINAL_FAILURE(-1..0),
        FAILURE(-3..-2),
        IMPRESSIVE_FAILURE(-5..-4),
        ASTOUNDING_FAILURE(Int.MIN_VALUE..-6),
    }
}
