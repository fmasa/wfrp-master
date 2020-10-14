package cz.muni.fi.rpg.ui.router

import androidx.compose.runtime.Immutable
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.encounter.NpcId
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import java.util.UUID

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

    class Compendium(val partyId: UUID) : Route()
}