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
    class GameMaster(val partyId: UUID) : Route()

    @Immutable
    class CharacterCreation(val partyId: UUID, val userId: String?) : Route()

    @Immutable
    class CharacterDetail(val characterId: CharacterId) : Route()

    @Immutable
    class CharacterEdit(val characterId: CharacterId) : Route()

    @Immutable
    class EncounterDetail(val encounterId: EncounterId) : Route()

    @Immutable
    class NpcDetail(val npcId: NpcId) : Route()

    @Immutable
    class NpcCreation(val encounterId: EncounterId) : Route()

    @Immutable
    class Compendium(val partyId: UUID) : Route()

    @Immutable
    class CompendiumImport(val partyId: UUID) : Route()

    object InvitationScanner : Route()
}