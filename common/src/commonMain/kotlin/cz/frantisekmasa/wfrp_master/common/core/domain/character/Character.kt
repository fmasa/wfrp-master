package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias LocalCharacterId = String

@Serializable
@Immutable
@Parcelize
data class Character(
    val id: LocalCharacterId,
    val type: CharacterType = CharacterType.PLAYER_CHARACTER,
    val name: String,
    val publicName: String? = null,
    val userId: UserId?,
    val career: String,
    val compendiumCareer: CompendiumCareer? = null,
    val socialClass: String,
    val status: SocialStatus = SocialStatus(SocialStatus.Tier.BRASS, 0),
    val psychology: String,
    val motivation: String,
    val race: Race?,
    val characteristicsBase: Stats,
    val characteristicsAdvances: Stats,
    val points: Points,
    val ambitions: Ambitions = Ambitions("", ""),
    val conditions: CurrentConditions = CurrentConditions.none(),
    val mutation: String = "",
    val note: String = "",
    @SerialName("hardyTalent") val hasHardyTalent: Boolean = false,
    val woundsModifiers: WoundsModifiers = WoundsModifiers(),
    val encumbranceBonus: Encumbrance = Encumbrance.Zero,
    @SerialName("archived") val isArchived: Boolean = false,
    val avatarUrl: String? = null,
    val money: Money = Money.ZERO,
    val hiddenTabs: Set<CharacterTab> = emptySet(),
    val size: Size? = null,
    // TODO: Drop default value after experience logs exist in all characters
    @RawValue
    val experienceLog: ImmutableList<ExperienceLogEntry>? = null,
) : Parcelable {

    val characteristics: Stats get() = characteristicsBase + characteristicsAdvances
    val wounds: Wounds
        get() = Wounds(
            points.wounds,
            calculateMaxWounds(
                size ?: race?.size,
                points,
                hasHardyTalent,
                woundsModifiers,
                characteristics
            ),
        )

    val maxEncumbrance: Encumbrance
        get() = Encumbrance.maximumForCharacter(characteristics) + encumbranceBonus

    init {
        require(userId == null || type == CharacterType.PLAYER_CHARACTER) {
            "Only Player Characters can have associated user"
        }
        require(publicName == null || publicName.isNotBlank()) { "Public name cannot be blank" }
        publicName?.requireMaxLength(NAME_MAX_LENGTH, "publicName")
        require(publicName == null || publicName.isNotBlank()) { "Public name cannot be blank" }
        require(publicName == null || type == CharacterType.NPC) {
            "Only NPC can have a public name"
        }
        require(name.isNotBlank())
        require(name.length <= NAME_MAX_LENGTH) { "Character name is too long" }
        require(career.length <= CAREER_MAX_LENGTH) { "Career is too long" }
        require(socialClass.length <= SOCIAL_CLASS_MAX_LENGTH) { "Social class is too long" }
        require(psychology.length <= PSYCHOLOGY_MAX_LENGTH) { "Psychology is too long" }
        require(motivation.length <= MOTIVATION_MAX_LENGTH) { "Motivation is too long" }
        require(mutation.length <= MUTATION_MAX_LENGTH) { "Mutation is too long" }
        require(note.length <= NOTE_MAX_LENGTH) { "Note is too long" }

        val maxWounds = calculateMaxWounds(
            size ?: race?.size,
            points,
            hasHardyTalent,
            woundsModifiers,
            characteristics,
        )
        require(points.wounds <= maxWounds) { "Wounds (${points.wounds} are greater than max Wounds ($maxWounds)" }
    }

    @Parcelize
    @Serializable
    data class CompendiumCareer(
        @Contextual val careerId: Uuid,
        @Contextual val levelId: Uuid,
    ) : Parcelable

    fun updateCharacteristics(base: Stats, advances: Stats): Character {
        return copy(
            characteristicsBase = base,
            characteristicsAdvances = advances,
            points = points.copy(
                wounds = points.wounds.coerceAtMost(
                    calculateMaxWounds(
                        size ?: race?.size,
                        points,
                        hasHardyTalent,
                        woundsModifiers,
                        base + advances,
                    )
                )
            )
        )
    }

    fun changeSize(size: Size?): Character {
        return copy(
            size = size,
            points = points.copy(
                wounds = points.wounds.coerceAtMost(
                    calculateMaxWounds(
                        size ?: race?.size,
                        points,
                        hasHardyTalent,
                        woundsModifiers,
                        characteristics
                    )
                )
            )
        )
    }

    fun modifyWounds(woundsModifiers: WoundsModifiers): Character {
        return copy(
            woundsModifiers = woundsModifiers,
            points = points.copy(
                wounds = points.wounds.coerceAtMost(
                    calculateMaxWounds(
                        size ?: race?.size,
                        points,
                        hasHardyTalent,
                        woundsModifiers,
                        characteristics
                    )
                )
            )
        )
    }

    fun spendExperience(
        amount: Int,
        currentTime: Instant,
        reason: String,
        userId: UserId,
    ): Character {
        require(amount >= 0) { "Cannot spend non-positive amount of experience" }
        require(amount <= points.experience) { "Cannot spend more experience than available" }

        val newPoints = points.copy(
            experience = points.experience - amount,
            spentExperience = points.spentExperience + amount,
        )

        return copy(
            points = newPoints,
            experienceLog = (experienceLog ?: defaultExperienceLog(newPoints, currentTime, userId))
                .plus(
                    ExperienceLogEntry(
                        id = uuid4(),
                        amount = -amount,
                        reason = reason,
                        createdAt = currentTime,
                        userId = userId,
                    )
                ).toImmutableList(),
        )
    }

    fun gainExperience(
        amount: Int,
        currentTime: Instant,
        reason: String,
        userId: UserId,
    ): Character {
        require(amount >= 0) { "Cannot gain non-positive amount of experience" }

        val newPoints = points.copy(experience = points.experience + amount)

        return copy(
            points = newPoints,
            experienceLog = (experienceLog ?: defaultExperienceLog(newPoints, currentTime, userId))
                .plus(
                    ExperienceLogEntry(
                        id = uuid4(),
                        amount = amount,
                        reason = reason,
                        createdAt = currentTime,
                        userId = userId,
                    )
                ).toImmutableList(),
        )
    }
    fun modifyEncumbranceBonus(bonus: Encumbrance): Character {
        return copy(encumbranceBonus = bonus)
    }

    fun updateCareer(
        careerName: String,
        socialClass: String,
        status: SocialStatus,
        compendiumCareer: CompendiumCareer?,
    ) = copy(
        career = careerName,
        socialClass = socialClass,
        status = status,
        compendiumCareer = compendiumCareer,
    )

    fun updateBasics(
        name: String,
        publicName: String?,
        race: Race?,
        motivation: String,
        note: String
    ) = copy(
        name = name,
        publicName = publicName,
        race = race,
        motivation = motivation,
        note = note,
        points = points.coerceWoundsAtMost(
            calculateMaxWounds(
                size ?: race?.size,
                points,
                hasHardyTalent,
                woundsModifiers,
                characteristics,
            )
        )
    )

    fun updateMaxWounds(maxWounds: Int?, hasHardyTalent: Boolean): Character {
        val newPoints = points.copy(maxWounds = maxWounds)
        return copy(
            hasHardyTalent = hasHardyTalent,
            points = newPoints.coerceWoundsAtMost(
                calculateMaxWounds(
                    size ?: race?.size,
                    newPoints,
                    hasHardyTalent,
                    woundsModifiers,
                    characteristics,
                )
            ),
        )
    }

    fun updateWellBeing(corruptionPoints: Int, psychology: String): Character {
        return copy(
            points = points.copy(corruption = corruptionPoints),
            psychology = psychology,
        )
    }

    fun updateMoneyBalance(money: Money) = copy(money = money)

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

    fun archive() = copy(
        isArchived = true,
        userId = null,
    )

    fun assignToUser(userId: UserId): Character {
        require(this.userId == null) {
            "Cannot assign Character that is already assigned to User"
        }
        require(type == CharacterType.PLAYER_CHARACTER) {
            "Only Player Characters can be assigned to User"
        }

        return copy(userId = userId)
    }

    fun unlinkFromUser() = copy(userId = null)

    fun turnIntoNPC() = copy(
        type = CharacterType.NPC,
        userId = null,
    )

    fun turnIntoPlayerCharacter() = copy(
        type = CharacterType.PLAYER_CHARACTER,
        publicName = null,
    )

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val CAREER_MAX_LENGTH = 50
        const val SOCIAL_CLASS_MAX_LENGTH = 50
        const val PSYCHOLOGY_MAX_LENGTH = 200
        const val MOTIVATION_MAX_LENGTH = 200
        const val MUTATION_MAX_LENGTH = 200
        const val NOTE_MAX_LENGTH = 2000

        private fun defaultExperienceLog(
            points: Points,
            currentTime: Instant,
            userId: UserId,
        ): List<ExperienceLogEntry> {
            if (points.experience == 0 && points.spentExperience == 0) {
                return persistentListOf()
            }

            return listOfNotNull(
                ExperienceLogEntry(
                    id = uuid4(),
                    amount = points.experience + points.spentExperience,
                    reason = "",
                    createdAt = currentTime,
                    userId = userId,
                ),
                ExperienceLogEntry(
                    id = uuid4(),
                    amount = -points.spentExperience,
                    reason = "",
                    createdAt = currentTime,
                    userId = userId,
                ).takeIf { points.spentExperience > 0 }
            )
        }

        private fun calculateMaxWounds(
            size: Size?,
            points: Points,
            hasHardyTalent: Boolean,
            modifiers: WoundsModifiers,
            characteristics: Stats,
        ): Int {
            val manualMaxWounds = points.maxWounds
            val toughnessBonus = characteristics.toughnessBonus

            if (manualMaxWounds != null) {
                // TODO: Remove support for hasHardyTalent flag
                if (hasHardyTalent) {
                    return manualMaxWounds + toughnessBonus
                }

                return manualMaxWounds
            }

            val baseWounds = Wounds.calculateMax(
                size = size ?: Size.AVERAGE,
                toughnessBonus = toughnessBonus,
                strengthBonus = characteristics.strengthBonus,
                willPowerBonus = if (modifiers.isConstruct)
                    characteristics.strengthBonus
                else characteristics.willPowerBonus
            )
            return (
                baseWounds +
                    modifiers.extraToughnessBonusMultiplier * toughnessBonus +
                    (if (hasHardyTalent) toughnessBonus else 0)
                ) *
                modifiers.afterMultiplier
        }
    }
}
