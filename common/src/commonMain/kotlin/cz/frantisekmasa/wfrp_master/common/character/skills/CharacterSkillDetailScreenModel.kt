package cz.frantisekmasa.wfrp_master.common.character.skills

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting

class CharacterSkillDetailScreenModel(
    characterId: CharacterId,
    private val skillRepository: SkillRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Skill>(
        characterId,
        skillRepository,
        userProvider,
        partyRepository,
    ) {
    suspend fun saveSkill(skill: Skill) {
        Reporting.record { characterItemAdded("skill") }
        skillRepository.save(characterId, skill)
    }
}
