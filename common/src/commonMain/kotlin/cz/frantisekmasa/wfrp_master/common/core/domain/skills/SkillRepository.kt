package cz.frantisekmasa.wfrp_master.common.core.domain.skills

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

interface SkillRepository : CharacterItemRepository<Skill> {
    suspend fun findByCompendiumId(characterId: CharacterId, compendiumSkillId: Uuid): Skill?
}
