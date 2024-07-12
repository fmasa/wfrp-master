package cz.frantisekmasa.wfrp_master.common.character

import cz.frantisekmasa.wfrp_master.common.character.characteristics.CharacteristicsScreenState
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreenState
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsScreenState
import cz.frantisekmasa.wfrp_master.common.character.notes.NotesScreenState
import cz.frantisekmasa.wfrp_master.common.character.religion.ReligionScreenState
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenState
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenState
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenState
import cz.frantisekmasa.wfrp_master.common.character.wellBeing.WellBeingScreenState
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import kotlinx.collections.immutable.ImmutableList

data class CharacterDetailScreenState(
    val characterId: CharacterId,
    val character: Character,
    val partyName: String,
    val isCombatActive: Boolean,
    val conditionsScreenState: ConditionsScreenState,
    val skillsScreenState: SkillsScreenState,
    val spellsScreenState: SpellsScreenState,
    val religionScreenState: ReligionScreenState,
    val characteristicsScreenState: CharacteristicsScreenState,
    val notesScreenState: NotesScreenState,
    val characterPickerState: CharacterPickerState,
    val trappingsScreenState: TrappingsScreenState,
    val wellBeingScreenState: WellBeingScreenState,
    val combatScreenState: CharacterCombatScreenState,
    val isGameMaster: Boolean,
)

data class CharacterPickerState(
    val allCharacters: ImmutableList<Character>,
    val assignableCharacters: ImmutableList<Character>,
)
