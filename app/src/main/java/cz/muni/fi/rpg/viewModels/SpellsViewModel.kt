package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.compendium.Compendium
import cz.muni.fi.rpg.model.domain.compendium.Spell as CompendiumSpell
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class SpellsViewModel(
    private val characterId: CharacterId,
    private val spellRepository: SpellRepository,
    private val compendium: Compendium<CompendiumSpell>
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val spells: Flow<List<Spell>> = spellRepository.findAllForCharacter(characterId)
    val compendiumSpellsCount: Flow<Int> by lazy { compendiumSpells.map { it.size } }

    val notUsedSpellsFromCompendium: Flow<List<cz.muni.fi.rpg.model.domain.compendium.Spell>> by lazy {
        compendiumSpells.zip(spells) { compendiumSpells, characterSpells ->
            val spellsUsedByCharacter = characterSpells.mapNotNull { it.compendiumId }.toSet()
            compendiumSpells.filter { !spellsUsedByCharacter.contains(it.id) }
        }
    }

    private val compendiumSpells by lazy { compendium.liveForParty(characterId.partyId) }

    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = launch {
        spellRepository.remove(characterId, spell.id)
    }
}