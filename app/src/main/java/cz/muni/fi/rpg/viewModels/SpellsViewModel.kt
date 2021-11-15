package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell as CompendiumSpell

class SpellsViewModel(
    private val characterId: CharacterId,
    private val spellRepository: SpellRepository,
    private val compendium: Compendium<CompendiumSpell>
) : ViewModel() {
    private val spellsFlow = spellRepository.findAllForCharacter(characterId)
    val spells: Flow<List<Spell>> = spellsFlow
    val compendiumSpellsCount: Flow<Int> by lazy { compendiumSpells.map { it.size } }

    val notUsedSpellsFromCompendium: Flow<List<CompendiumSpell>> by lazy {
        compendiumSpells.combineTransform(spellsFlow) { compendiumSpells, characterSpells ->
            val spellsUsedByCharacter = characterSpells.mapNotNull { it.compendiumId }.toSet()

            emit(compendiumSpells.filter { !spellsUsedByCharacter.contains(it.id) })
        }
    }

    private val compendiumSpells by lazy { compendium.liveForParty(characterId.partyId) }

    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = viewModelScope.launch(Dispatchers.IO) {
        spellRepository.remove(characterId, spell.id)
    }
}
