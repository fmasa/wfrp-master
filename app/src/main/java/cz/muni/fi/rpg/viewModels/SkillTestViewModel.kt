package cz.muni.fi.rpg.viewModels

import cz.frantisekmasa.wfrp_master.compendium.domain.Compendium
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.rolls.Dice
import cz.frantisekmasa.wfrp_master.core.domain.rolls.TestResult
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill as CompendiumSkill

class SkillTestViewModel(
    private val partyId: UUID,
    skillCompendium: Compendium<CompendiumSkill>,
    characterRepository: CharacterRepository,
    private val characterSkills: SkillRepository,
) {
    val characters: Flow<List<Character>> = characterRepository
        .inParty(partyId)

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

        return basicTestedValue(compendiumSkill, skill, character.getCharacteristics())
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