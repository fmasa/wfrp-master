package cz.frantisekmasa.wfrp_master.common.core.domain.injuries

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Injury as CompendiumInjury
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Countdown
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString

data class Injury(
    override val id: UuidAsString,
    val name: String,
    val duration: Countdown?,
    val location: String,
    val description: String,
    /**
     * Used as custom player note for injury where description is taken from compendium.
     */
    val note: String,
    override val compendiumId: Uuid?,
) : CharacterItem<Injury, CompendiumInjury> {

    init {
        name.requireMaxLength(CompendiumInjury.NAME_MAX_LENGTH, "name")
        note.requireMaxLength(NOTE_MAX_LENGTH, "note")
        description.requireMaxLength(CompendiumInjury.DESCRIPTION_MAX_LENGTH, "description")
    }

    override fun unlinkFromCompendium(): Injury = copy(compendiumId = null)

    override fun updateFromCompendium(compendiumItem: CompendiumInjury): Injury =
        copy(
            name = compendiumItem.name,
        )

    companion object {
        const val NOTE_MAX_LENGTH = 500

        fun fromCompendium(
            compendiumInjury: CompendiumInjury,
            duration: Countdown?,
            location: String,
            note: String,
        ): Injury =
            Injury(
                id = uuid4(),
                name = compendiumInjury.name,
                duration = duration,
                location = location,
                note = note,
                compendiumId = compendiumInjury.id,
                description = compendiumInjury.description,
            )
    }
}