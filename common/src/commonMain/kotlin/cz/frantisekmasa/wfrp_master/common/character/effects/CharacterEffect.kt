package cz.frantisekmasa.wfrp_master.common.character.effects

import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character

interface CharacterEffect {
    fun apply(character: Character, otherEffects: List<CharacterEffect>): Character
    fun revert(character: Character, otherEffects: List<CharacterEffect>): Character
}
