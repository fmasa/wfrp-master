package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CharacterScreenModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    private val avatarChanger: CharacterAvatarChanger,
    careerCompendium: Compendium<Career>,
    private val userProvider: UserProvider,
    partyRepository: PartyRepository,
) : ScreenModel {

    val character: Flow<Character> = characters.getLive(characterId).right()
    val allCharacters: Flow<List<Character>> =
        characters.inParty(characterId.partyId, CharacterType.PLAYER_CHARACTER)

    private val party = partyRepository.getLive(characterId.partyId).right()

    val allCareers: Flow<List<Career>> = careerCompendium.liveForParty(characterId.partyId)
        .combine(party) { careers, party ->
            if (userProvider.userId == party.gameMasterId) {
                return@combine careers
            }

            careers.filter { it.isVisibleToPlayers }
        }

    val career: Flow<CurrentCareer?> = combine(character, allCareers) { character, allCareers ->
        val currentCareer = character.compendiumCareer ?: return@combine null

        val career = allCareers.firstOrNull { it.id == currentCareer.careerId }
            ?: return@combine null

        val level = career.levels.firstOrNull { it.id == currentCareer.levelId }
            ?: return@combine null

        CurrentCareer(career, level)
    }

    @Immutable
    data class CurrentCareer(
        val career: Career,
        val level: Career.Level,
    )

    suspend fun update(change: (Character) -> Character) {
        val character = characters.get(characterId)

        characters.save(characterId.partyId, change(character))
    }

    suspend fun characterExists(): Boolean {
        return try {
            characters.get(characterId)

            true
        } catch (e: CharacterNotFound) {
            false
        }
    }

    suspend fun archive() {
        // TODO: Remove this character from combat (see [Combat::removeNpc()])
        val character = characters.get(characterId)

        characters.save(characterId.partyId, character.archive())
    }

    suspend fun changeAvatar(image: ByteArray) {
        avatarChanger.changeAvatar(characterId, image)
    }

    suspend fun removeAvatar() {
        avatarChanger.removeAvatar(characterId)
    }
}
