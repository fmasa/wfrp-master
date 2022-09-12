package cz.frantisekmasa.wfrp_master.common.characterCreation

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CharacterCreationScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
    private val careerCompendium: Compendium<Career>,
) : ScreenModel {

    suspend fun getCareers(): List<Career> {
        return careerCompendium.liveForParty(partyId).first()
    }

    suspend fun createCharacter(
        userId: UserId?,
        type: CharacterType,
        info: CharacterBasicInfoForm.Data,
        characteristicsData: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
    ): CharacterId {
        val characterId = CharacterId(partyId, uuid4().toString())

        return withContext(Dispatchers.IO) {
            try {
                Napier.d("Creating character")

                val characteristics = characteristicsData.toValue()
                val customCareer = info.customCareer.value
                val careerLevel = info.careers
                    .asSequence()
                    .flatMap { it.levels }
                    .firstOrNull { it.id == info.compendiumCareer.value?.levelId }

                characters.save(
                    characterId.partyId,
                    Character(
                        id = characterId.id,
                        type = type,
                        name = info.name.value,
                        publicName = info.publicName.value.takeIf { it.isNotBlank() },
                        userId = userId?.toString(),
                        career = if (customCareer) info.career.value else "",
                        socialClass = if (customCareer) info.socialClass.value else "",
                        status = careerLevel?.status ?: info.status.value,
                        race = info.race.value,
                        characteristicsBase = characteristics.base,
                        characteristicsAdvances = characteristics.advances,
                        points = points.toValue(),
                        psychology = info.psychology.value,
                        motivation = info.motivation.value,
                        note = info.note.value,
                        compendiumCareer = if (!customCareer) info.compendiumCareer.value else null,
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
