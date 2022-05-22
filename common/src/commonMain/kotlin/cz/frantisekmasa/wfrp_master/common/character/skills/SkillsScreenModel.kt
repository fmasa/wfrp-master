package cz.frantisekmasa.wfrp_master.common.character.skills

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class SkillsScreenModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository,
    private val compendium: Compendium<CompendiumSkill>
) : CharacterItemScreenModel<Skill, CompendiumSkill>(characterId, skillRepository, compendium) {
    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun saveCompendiumSkill(skillId: Uuid, compendiumSkillId: Uuid, advances: Int) {
        val compendiumSkill = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumSkillId,
        )

        skillRepository.save(
            characterId,
            Skill(
                id = skillId,
                compendiumId = compendiumSkill.id,
                advanced = compendiumSkill.advanced,
                characteristic = compendiumSkill.characteristic,
                name = compendiumSkill.name,
                description = compendiumSkill.description,
                advances = advances,
            )
        )
    }

    fun removeSkill(skill: Skill) = coroutineScope.launch(Dispatchers.IO) {
        skillRepository.remove(characterId, skill.id)
    }
}
