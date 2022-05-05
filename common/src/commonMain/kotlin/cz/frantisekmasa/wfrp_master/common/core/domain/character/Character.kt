package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Character(
    val name: String,
    val userId: String?,
    val career: String,
    val socialClass: String,
    val status: SocialStatus = SocialStatus(SocialStatus.Tier.BRASS, 0),
    val psychology: String,
    val motivation: String,
    val race: Race,
    val characteristicsBase: Stats,
    val characteristicsAdvances: Stats,
    val points: Points,
    val ambitions: Ambitions = Ambitions("", ""),
    val conditions: CurrentConditions = CurrentConditions.none(),
    val mutation: String = "",
    val note: String = "",
    @SerialName("hardyTalent") val hasHardyTalent: Boolean = false,
    @SerialName("archived") val isArchived: Boolean = false,
    val avatarUrl: String? = null,
    val id: String = userId ?: error("Either ID or UserId must be present"), // TODO: Remove this fallback in 1.14
    val money: Money = Money.zero(),
    val hiddenTabs: Set<CharacterTab> = emptySet(),
) {

    val characteristics: Stats get() = characteristicsBase + characteristicsAdvances
    val wounds: Wounds get() = Wounds(points.wounds, calculateMaxWounds(race, points, hasHardyTalent, characteristics))

    init {
        require(listOf(name, userId, career).all { it?.isNotBlank() ?: true })
        require(name.length <= NAME_MAX_LENGTH) { "Character name is too long" }
        require(career.length <= CAREER_MAX_LENGTH) { "Career is too long" }
        require(socialClass.length <= SOCIAL_CLASS_MAX_LENGTH) { "Social class is too long" }
        require(psychology.length <= PSYCHOLOGY_MAX_LENGTH) { "Psychology is too long" }
        require(motivation.length <= MOTIVATION_MAX_LENGTH) { "Motivation is too long" }
        require(mutation.length <= MUTATION_MAX_LENGTH) { "Mutation is too long" }
        require(note.length <= NOTE_MAX_LENGTH) { "Note is too long" }
        require(!isArchived || userId == null) { "Only non-user-characters can be archived" }

        val maxWounds = calculateMaxWounds(race, points, hasHardyTalent, characteristics)
        require(points.wounds <= maxWounds) { "Wounds (${points.wounds} are greater than max Wounds ($maxWounds)" }
    }

    fun updateCharacteristics(base: Stats, advances: Stats): Character {
        return copy(
            characteristicsBase = base,
            characteristicsAdvances = advances,
            points = points.copy(
                wounds = points.wounds.coerceAtMost(
                    calculateMaxWounds(race, points, hasHardyTalent, base + advances)
                )
            )
        )
    }

    fun updateCareer(careerName: String, socialClass: String, status: SocialStatus) = copy(
        career = careerName,
        socialClass = socialClass,
        status = status,
    )

    fun updateBasics(name: String, race: Race, motivation: String) = copy(
        name = name,
        race = race,
        motivation = motivation,
        points = points.coerceWoundsAtMost(
            calculateMaxWounds(race, points, hasHardyTalent, characteristics)
        )
    )

    fun updateMaxWounds(maxWounds: Int?, hasHardyTalent: Boolean): Character {
        val newPoints = points.copy(maxWounds =  maxWounds)
        return copy(
            hasHardyTalent = hasHardyTalent,
            points = newPoints.coerceWoundsAtMost(
                calculateMaxWounds(race, newPoints, hasHardyTalent, characteristics)
            ),
        )
    }

    fun updateWellBeing(corruptionPoints: Int, psychology: String): Character {
        return copy(
            points = points.copy(corruption = corruptionPoints),
            psychology = psychology,
        )
    }

    fun addMoney(amount: Money) = copy(money = money + amount)

    fun updateHiddenTabs(hiddenTabs: Set<CharacterTab>) = copy(hiddenTabs = hiddenTabs)

    /**
     * @throws NotEnoughMoney
     */
    fun subtractMoney(amount: Money): Character {
        try {
            return copy(money = money - amount)
        } catch (e: IllegalArgumentException) {
            throw NotEnoughMoney(amount, e)
        }
    }

    fun refreshWounds(): Character = copy(
        points = points.copy(wounds = wounds.max)
    )

    fun updatePoints(newPoints: Points) = copy(points = newPoints)

    fun updateAmbitions(ambitions: Ambitions) = copy(ambitions = ambitions)

    fun updateConditions(newConditions: CurrentConditions) = copy(conditions = newConditions)

    fun archive() = copy(isArchived = true)

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val CAREER_MAX_LENGTH = 50
        const val SOCIAL_CLASS_MAX_LENGTH = 50
        const val PSYCHOLOGY_MAX_LENGTH = 200
        const val MOTIVATION_MAX_LENGTH = 200
        const val MUTATION_MAX_LENGTH = 200
        const val NOTE_MAX_LENGTH = 400

        private fun calculateMaxWounds(
            race: Race,
            points: Points,
            hasHardyTalent: Boolean,
            characteristics: Stats,
        ): Int {
            val baseWounds = points.maxWounds ?: Wounds.calculateMax(race.size, characteristics)

            return if (hasHardyTalent) // TODO: Support multiple Hardy that is multiple times taken
                baseWounds + characteristics.toughnessBonus
            else baseWounds
        }
    }
}
