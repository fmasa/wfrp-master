package cz.frantisekmasa.wfrp_master.common.skillTest

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.TestResult
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import kotlinx.coroutines.flow.Flow
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
        val skill = characterSkills.findByCompendiumId(
            CharacterId(partyId, character.id),
            compendiumSkill.id
        )

        return basicTestedValue(compendiumSkill, skill, character.characteristics)
            ?.let {
                TestResult(
                    rollValue = Dice(100).roll(),
                    testedValue = it + testModifier
                )
            }
    }

    private fun basicTestedValue(
        compendiumSkill: CompendiumSkill,
        characterSkill: Skill?,
        characteristics: Stats
    ): Int? {
        val characteristic = compendiumSkill.characteristic.characteristicValue(characteristics)

        // Character has at least one skill advance
        if (characterSkill != null) {
            return characteristic + characterSkill.advances
        }

        // Characters cannot use advanced skills that they don't have at least one advance in
        // see Basic and Advanced Skills on page 117 of Rulebook
        if (compendiumSkill.advanced) {
            return null
        }

        return characteristic
    }
}
