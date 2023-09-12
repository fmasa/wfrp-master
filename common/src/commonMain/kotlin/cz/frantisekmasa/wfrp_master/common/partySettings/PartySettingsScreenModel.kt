package cz.frantisekmasa.wfrp_master.common.partySettings

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.settings.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PartySettingsScreenModel(
    private val partyId: PartyId,
    private val parties: PartyRepository,
    private val characters: CharacterRepository,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
) : ScreenModel {
    val party: Flow<Party> = parties.getLive(partyId).right()

    suspend fun updateSettings(change: (Settings) -> Settings) {
        parties.update(partyId) {
            it.updateSettings(change(it.settings))
        }
    }

    suspend fun renameParty(newName: String) {
        parties.update(partyId) { it.rename(newName) }
    }

    suspend fun changeLanguage(language: Language) {
        val party = parties.get(partyId)

        if (party.settings.language == language) {
            return
        }

        firestore.runTransaction { transaction ->
            parties.save(
                transaction,
                party.updateSettings(party.settings.copy(language = language)),
            )

            val allCharacters = characters.inParty(partyId, CharacterType.values().toSet())
                .first()
            for (character in allCharacters) {
                effectManager.reapplyWithDifferentLanguage(
                    transaction,
                    partyId,
                    character,
                    party.settings.language,
                    language,
                )
            }
        }
    }
}
