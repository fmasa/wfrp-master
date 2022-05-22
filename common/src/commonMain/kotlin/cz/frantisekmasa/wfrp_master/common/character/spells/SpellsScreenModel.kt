package cz.frantisekmasa.wfrp_master.common.character.spells

import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell as CompendiumSpell

class SpellsScreenModel(
    private val characterId: cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId,
    private val spellRepository: SpellRepository,
    compendium: Compendium<CompendiumSpell>
) : CharacterItemScreenModel<Spell, CompendiumSpell>(characterId, spellRepository, compendium) {
    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = coroutineScope.launch(Dispatchers.IO) {
        spellRepository.remove(characterId, spell.id)
    }
}
