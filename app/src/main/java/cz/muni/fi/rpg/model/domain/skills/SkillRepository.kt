package cz.muni.fi.rpg.model.domain.skills

import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import java.util.UUID

interface SkillRepository : CharacterItemRepository<Skill> {
    suspend fun findByCompendiumId(characterId: CharacterId, compendiumSkillId: UUID): Skill?
}
