package cz.frantisekmasa.wfrp_master.common.compendium.career

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.flow.Flow

class CareerCompendiumScreenModel(
    private val partyId: PartyId,
    private val firestore: Firestore,
    compendium: Compendium<Career>,
    private val characters: CharacterRepository,
) : CompendiumItemScreenModel<Career>(partyId, compendium) {
    val careers: Flow<List<Career>> = compendium.liveForParty(partyId)

    suspend fun saveLevel(careerId: Uuid, level: Career.Level) {
        val career = compendium.getItem(partyId, careerId)
        val existingIndex = career.levels.indexOfFirst { it.id == level.id }

        if (existingIndex == -1) {
            compendium.saveItems(partyId, listOf(career.copy(levels = career.levels + level)))
        } else {
            val levels = career.levels.toMutableList()
            levels[existingIndex] = level

            compendium.saveItems(partyId, listOf(career.copy(levels = levels)))
        }
    }

    override suspend fun update(compendiumItem: Career) {
        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, compendiumItem)
        }
    }

    override suspend fun remove(compendiumItem: Career) {
        val charactersWithCareer = characters.findByCompendiumCareer(partyId, compendiumItem.id)

        firestore.runTransaction { transaction ->
            compendium.remove(transaction, partyId, compendiumItem)

            charactersWithCareer.forEach { character ->
                characters.save(
                    transaction,
                    partyId,
                    unlinkCharacterFromCompendiumCareer(character, compendiumItem),
                )
            }
        }
    }

    private fun unlinkCharacterFromCompendiumCareer(
        character: Character,
        previousCareerVersion: Career,
    ): Character {
        val characterCareer = character.compendiumCareer ?: return character

        val level = previousCareerVersion.levels.firstOrNull { it.id == characterCareer.levelId }

        return character.updateCareer(
            careerName = level?.name ?: "",
            socialClass = character.socialClass,
            status = character.status,
            compendiumCareer = null,
        )
    }
}
