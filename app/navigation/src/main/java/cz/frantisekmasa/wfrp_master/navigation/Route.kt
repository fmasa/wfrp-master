package cz.frantisekmasa.wfrp_master.navigation

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import java.util.*

sealed class Route {
    @Immutable
    object PartyList : Route()

    @Immutable
    object About : Route()

    @Immutable
    object Settings : Route()

    @Immutable
    data class GameMaster(val partyId: PartyId) : Route()

    @Immutable
    data class PartySettings(val partyId: PartyId) : Route()

    @Immutable
    data class CharacterCreation(val partyId: PartyId, val userId: String?) : Route()

    @Immutable
    data class CharacterDetail(val characterId: CharacterId) : Route()

    @Immutable
    data class CharacterEdit(val characterId: CharacterId) : Route()

    @Immutable
    data class EncounterDetail(val encounterId: EncounterId) : Route()

    @Immutable
    data class NpcDetail(val npcId: NpcId) : Route()

    @Immutable
    data class NpcCreation(val encounterId: EncounterId) : Route()

    @Immutable
    data class Compendium(val partyId: PartyId) : Route()

    @Immutable
    data class CompendiumImport(val partyId: PartyId) : Route()

    object InvitationScanner : Route()

    @Immutable
    data class ActiveCombat(val partyId: PartyId) : Route()
}