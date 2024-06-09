package cz.frantisekmasa.wfrp_master.common.encounters

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterNotFound
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.npcs.NpcList
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class EncounterDetailScreenModel(
    val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val characters: CharacterRepository,
    private val parties: PartyRepository,
) : ScreenModel {
    val encounter: Flow<Encounter> = encounters.getLive(encounterId).right()
    val notUsedNpcs: Flow<ImmutableList<NpcList.Item>> =
        combine(
            encounter,
            characters.inParty(encounterId.partyId, CharacterType.NPC),
        ) { encounter, npcs ->
            npcs
                .asSequence()
                .filter { it.id !in encounter.characters }
                .map { npc ->
                    NpcList.Item(
                        id = npc.id,
                        name = npc.name,
                        avatarUrl = npc.avatarUrl,
                    )
                }
                .toImmutableList()
        }

    private val npcs: Flow<List<Character>> =
        encounter
            .map { it.characters.keys }
            .distinctUntilChanged()
            .flatMapLatest { characters.findByIds(encounterId.partyId, it) }
            .map { characters -> characters.values.sortedBy { it.name } }

    val npcsInEncounter: Flow<ImmutableList<NpcInEncounter>> =
        combine(npcs, encounter) { npcs, encounter ->
            npcs.map { npc ->
                NpcInEncounter(
                    id = npc.id,
                    name = npc.name,
                    avatarUrl = npc.avatarUrl,
                    count = encounter.characters[npc.id] ?: 0,
                )
            }.toImmutableList()
        }

    suspend fun remove() {
        parties.update(encounterId.partyId) { party ->
            if (party.activeCombat?.encounterId == encounterId.encounterId) {
                party.endCombat()
            } else {
                party
            }
        }

        encounters.remove(encounterId)
    }

    suspend fun addNewNpc(npcId: LocalCharacterId) {
        try {
            encounters.update(encounterId) { encounter ->
                if (npcId in encounter.characters) {
                    encounter
                } else {
                    encounter.withCharacterCount(npcId, 1)
                }
            }
        } catch (e: EncounterNotFound) {
            Napier.e("Encounter $encounterId not found: $e")
        }
    }

    suspend fun updateEncounter(encounter: Encounter) {
        encounters.save(encounterId.partyId, encounter)
    }
}
