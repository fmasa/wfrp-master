package cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Plurals
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease as CompendiumDisease

@Parcelize
@Serializable
data class Disease(
    override val id: UuidAsString,
    val name: String,
    /**
     * Used as custom player note for trappings where description is taken from compendium.
     */
    val note: String = "",
    val description: String,
    val symptoms: List<String>,
    val permanentEffects: String,
    val incubation: Countdown,
    val duration: Countdown,
    val isDiagnosed: Boolean,
    override val compendiumId: UuidAsString? = null,
) : CharacterItem<Disease, CompendiumDisease> {
    init {
        name.requireMaxLength(CompendiumDisease.NAME_MAX_LENGTH, "name")
        note.requireMaxLength(NOTE_MAX_LENGTH, "note")
        permanentEffects.requireMaxLength(CompendiumDisease.PERMANENT_EFFECTS_MAX_LENGTH, "permanentEffects")
        description.requireMaxLength(CompendiumDisease.DESCRIPTION_MAX_LENGTH, "description")
    }

    @Stable
    val isHealed: Boolean get() = duration.value == 0

    override fun updateFromCompendium(compendiumItem: CompendiumDisease) =
        copy(
            name = compendiumItem.name,
            symptoms = compendiumItem.symptoms,
            permanentEffects = compendiumItem.permanentEffects,
        )

    override fun unlinkFromCompendium(): Disease = copy(compendiumId = null)

    companion object {
        const val NOTE_MAX_LENGTH = 500

        fun fromCompendium(
            compendiumDisease: CompendiumDisease,
            incubation: Countdown,
            duration: Countdown,
            isDiagnosed: Boolean,
        ): Disease =
            Disease(
                id = uuid4(),
                name = compendiumDisease.name,
                description = compendiumDisease.description,
                symptoms = compendiumDisease.symptoms,
                permanentEffects = compendiumDisease.permanentEffects,
                incubation = incubation,
                duration = duration,
                isDiagnosed = isDiagnosed,
                compendiumId = compendiumDisease.id,
            )
    }
}

@Parcelize
@Serializable
@Immutable
data class Countdown(
    val value: Int,
    val unit: Unit,
) : Parcelable {
    init {
        require(value >= 0) { "Remaining time must be non-negative" }
    }

    enum class Unit(
        override val translatableName: StringResource,
        val plural: PluralsResource,
    ) : NamedEnum {
        DAYS(Str.common_ui_units_days, Plurals.duration_days),
        HOURS(Str.common_ui_units_hours, Plurals.duration_hours),
        MINUTES(Str.common_ui_units_minutes, Plurals.duration_minutes),
    }

    @Composable
    @Stable
    fun toText(): String = stringResource(unit.plural, value, value)
}
