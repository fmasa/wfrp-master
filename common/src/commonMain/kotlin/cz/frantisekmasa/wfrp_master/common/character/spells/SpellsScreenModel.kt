package cz.frantisekmasa.wfrp_master.common.character.spells

import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell as CompendiumSpell

class SpellsScreenModel(
    characterId: CharacterId,
    private val spellRepository: SpellRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
    compendium: Compendium<CompendiumSpell>
) : CharacterItemScreenModel<Spell, CompendiumSpell>(
    characterId,
    spellRepository,
    compendium,
    userProvider,
    partyRepository,
) {
    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = coroutineScope.launch(Dispatchers.IO) {
        spellRepository.remove(characterId, spell.id)
    }
}
