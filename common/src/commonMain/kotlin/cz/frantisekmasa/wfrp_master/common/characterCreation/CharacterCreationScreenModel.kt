package cz.frantisekmasa.wfrp_master.common.characterCreation

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CharacterCreationScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository
) : ScreenModel {

    suspend fun createCharacter(
        userId: UserId?,
        type: CharacterType,
        info: CharacterBasicInfoForm.Data,
        characteristicsData: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
    ): CharacterId {
        val characterId = CharacterId(partyId, UUID.randomUUID().toString())

        return withContext(Dispatchers.IO) {
            try {
                Napier.d("Creating character")

                val characteristics = characteristicsData.toValue()

                characters.save(
                    characterId.partyId,
                    Character(
                        id = characterId.id,
                        type = type,
                        name = info.name.value,
                        userId = userId?.toString(),
                        career = info.career.value,
                        socialClass = info.socialClass.value,
                        status = SocialStatus(info.socialTier.value, info.socialStanding.value),
                        race = info.race.value,
                        characteristicsBase = characteristics.base,
                        characteristicsAdvances = characteristics.advances,
                        points = points.toValue(),
                        psychology = info.psychology.value,
                        motivation = info.motivation.value,
                        note = info.note.value,
                    ).refreshWounds()
                )

                Reporter.recordEvent(
                    "create_character",
                    mapOf(
                        "party_id" to characterId.partyId.toString(),
                        "character_id" to characterId.id
                    )
                )

                characterId
            } catch (e: Throwable) {
                Napier.e(e.toString(), e)
                throw e
            }
        }
    }
}
