package cz.frantisekmasa.wfrp_master.navigation

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import java.util.*

sealed class Route {
    @Immutable
    object PartyList : Route()

    @Immutable
    object About : Route()

    @Immutable
    object Settings : Route()

    @Immutable
    data class GameMaster(val partyId: UUID) : Route()

    @Immutable
    data class PartySettings(val partyId: UUID) : Route()

    @Immutable
    data class CharacterCreation(val partyId: UUID, val userId: String?) : Route()

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
    data class Compendium(val partyId: UUID) : Route()

    @Immutable
    data class CompendiumImport(val partyId: UUID) : Route()

    object InvitationScanner : Route()

    @Immutable
    data class ActiveCombat(val partyId: UUID) : Route()
}