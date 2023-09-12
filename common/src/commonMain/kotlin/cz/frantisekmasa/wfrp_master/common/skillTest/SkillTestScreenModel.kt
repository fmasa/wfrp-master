package cz.frantisekmasa.wfrp_master.common.skillTest

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.TestResult
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class SkillTestScreenModel(
    private val partyId: PartyId,
    skillCompendium: Compendium<CompendiumSkill>,
    characterRepository: CharacterRepository,
    private val characterSkills: SkillRepository,
) : ScreenModel {
    val characters: Flow<List<Character>> = characterRepository.inParty(partyId, CharacterType.PLAYER_CHARACTER)
    val skills: Flow<List<CompendiumSkill>> = skillCompendium.liveForParty(partyId)

    suspend fun performSkillTest(
        character: Character,
        compendiumSkill: CompendiumSkill,
        testModifier: Int
    ): TestResult? {
        val characteristic = character.characteristics.get(compendiumSkill.characteristic)
        val advances = characterSkills.findAllForCharacter(CharacterId(partyId, character.id))
            .first()
            .asSequence()
            .filter { it.characteristic == compendiumSkill.characteristic }
            .filter { it.name.equals(compendiumSkill.name, ignoreCase = true) }
            .maxOfOrNull { it.advances }

        // Characters cannot use advanced skills that they don't have at least one advance in
        // see Basic and Advanced Skills on page 117 of Rulebook
        if (advances == null && compendiumSkill.advanced) {
            return null
        }

        return TestResult(
            rollValue = d100.roll(),
            testedValue = characteristic + (advances ?: 0) + testModifier,
        )
    }

    companion object {
        private val d100 = Dice(100)
    }
}
