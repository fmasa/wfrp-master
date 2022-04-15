package cz.frantisekmasa.wfrp_master.common.character.spells

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.SpellRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell as CompendiumSpell

class SpellsScreenModel(
    private val characterId: cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId,
    private val spellRepository: SpellRepository,
    private val compendium: Compendium<CompendiumSpell>
) : ScreenModel {
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

    fun removeSpell(spell: Spell) = coroutineScope.launch(Dispatchers.IO) {
        spellRepository.remove(characterId, spell.id)
    }
}
