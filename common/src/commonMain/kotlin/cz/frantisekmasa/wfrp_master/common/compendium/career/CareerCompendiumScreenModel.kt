package cz.frantisekmasa.wfrp_master.common.compendium.career

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.localization.Localization
import kotlinx.coroutines.flow.Flow

class CareerCompendiumScreenModel(
    private val partyId: PartyId,
    private val firestore: Firestore,
    private val compendium: Compendium<Career>,
    private val characters: CharacterRepository,
) : ScreenModel {
    val careers: Flow<List<Career>> = compendium.liveForParty(partyId)

    fun getCareer(partyId: PartyId, careerId: Uuid): Flow<Either<CompendiumItemNotFound, Career>> {
        return compendium.getLive(partyId, careerId)
    }

    suspend fun createNew(career: Career) {
        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, career)
        }
    }

    suspend fun saveLevel(careerId: Uuid, level: Career.Level) {
        val career = compendium.getItem(partyId, careerId)
        val existingIndex = career.levels.indexOfFirst { it.id == level.id }

        if (existingIndex == -1) {
            compendium.saveItems(partyId, career.copy(levels = career.levels + level))
        } else {
            val levels = career.levels.toMutableList()
            levels[existingIndex] = level

            compendium.saveItems(partyId, career.copy(levels = levels))
        }
    }

    suspend fun deleteLevel(careerId: Uuid, level: Career.Level) {
        val career = compendium.getItem(partyId, careerId)
        val charactersWithCareer = characters.findByCompendiumCareer(partyId, careerId)

        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, career)

            charactersWithCareer
                .asSequence()
                .filter { it.compendiumCareer?.levelId == level.id }
                .forEach { character ->
                    characters.save(
                        transaction,
                        partyId,
                        unlinkCharacterFromCompendiumCareer(character, career),
                    )
                }
        }
    }

    suspend fun update(career: Career) {
        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, career)
        }
    }

    suspend fun remove(career: Career) {
        val charactersWithCareer = characters.findByCompendiumCareer(partyId, career.id)

        firestore.runTransaction { transaction ->
            compendium.remove(transaction, partyId, career)

            charactersWithCareer.forEach { character ->
                characters.save(
                    transaction,
                    partyId,
                    unlinkCharacterFromCompendiumCareer(character, career),
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
            socialClass = previousCareerVersion.socialClass.nameResolver(Localization.English),
            status = character.status,
            compendiumCareer = null,
        )
    }
}
