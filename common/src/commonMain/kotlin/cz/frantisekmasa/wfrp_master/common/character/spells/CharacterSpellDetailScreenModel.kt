package cz.frantisekmasa.wfrp_master.common.character.spells

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository

class CharacterSpellDetailScreenModel(
    characterId: CharacterId,
    private val spellRepository: SpellRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Spell>(
        characterId,
        spellRepository,
        userProvider,
        partyRepository,
    ) {
    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }
}
