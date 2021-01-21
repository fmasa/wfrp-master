package cz.frantisekmasa.wfrp_master.navigation

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import java.util.*

sealed class Route {
    @Immutable
    object PartyList : Route() {
        override fun toString() = "parties"
    }

    @Immutable
    object About : Route() {
        override fun toString() = "about"
    }

    @Immutable
    object Settings : Route() {
        override fun toString() = "settings"
    }

    @Immutable
    data class GameMaster(val partyId: PartyId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/gameMaster"
            fun fromEntry(entry: NavBackStackEntry) = GameMaster(
                PartyId.fromString(entry.stringArgument("partyId"))
            )
        }

        override fun toString() = "parties/$partyId/gameMaster"
    }

    @Immutable
    data class PartySettings(val partyId: PartyId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/settings"
            fun fromEntry(entry: NavBackStackEntry) = PartySettings(
                PartyId.fromString(entry.stringArgument("partyId"))
            )
        }

        override fun toString() = "parties/$partyId/settings"
    }

    @Immutable
    data class CharacterCreation(val partyId: PartyId, val userId: String?) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/new-character?userId={userId}"
            fun fromEntry(entry: NavBackStackEntry) = CharacterCreation(
                PartyId.fromString(entry.stringArgument("partyId")),
                entry.arguments?.getString("userId")
            )
        }

        override fun toString() = "parties/$partyId/new-character?userId=${userId ?: ""}"
    }

    @Immutable
    data class CharacterDetail(
        val characterId: CharacterId,
        val comingFromCombat: Boolean = false
    ) : Route() {
        companion object {
            override fun toString() =
                "parties/{partyId}/characters/{characterId}?comingFromCombat={comingFromCombat}"

            fun fromEntry(entry: NavBackStackEntry) = CharacterDetail(
                CharacterId(
                    PartyId.fromString(entry.stringArgument("partyId")),
                    entry.stringArgument("characterId"),
                ),
                entry.arguments?.getString("fromCombat") == "1",
            )
        }

        override fun toString() =
            "parties/${characterId.partyId}/characters/${characterId.id}?comingFromCombat=${if (comingFromCombat) 1 else 0}"
    }

    @Immutable
    data class CharacterEdit(val characterId: CharacterId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/character-edit/{characterId}"
            fun fromEntry(entry: NavBackStackEntry) = CharacterEdit(
                CharacterId(
                    PartyId.fromString(entry.stringArgument("partyId")),
                    entry.stringArgument("characterId"),
                )
            )
        }

        override fun toString() = "parties/${characterId.partyId}/character-edit/${characterId.id}"
    }

    @Immutable
    data class EncounterDetail(val encounterId: EncounterId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/encounters/{encounterId}"
            fun fromEntry(entry: NavBackStackEntry) = EncounterDetail(
                EncounterId(
                    PartyId.fromString(entry.stringArgument("partyId")),
                    UUID.fromString(entry.stringArgument("encounterId")),
                )
            )
        }

        override fun toString() =
            "parties/${encounterId.partyId}/encounters/${encounterId.encounterId}"
    }

    @Immutable
    data class NpcDetail(val npcId: NpcId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/encounters/{encounterId}/npcs/{npcId}"
            fun fromEntry(entry: NavBackStackEntry) = NpcDetail(
                NpcId(
                    EncounterId(
                        PartyId.fromString(entry.stringArgument("partyId")),
                        UUID.fromString(entry.stringArgument("encounterId")),
                    ),
                    UUID.fromString(entry.stringArgument("npcId"))
                )
            )
        }

        override fun toString() =
            "parties/${npcId.encounterId.partyId}/encounters/${npcId.encounterId.encounterId}/npcs/${npcId.npcId}"
    }

    @Immutable
    data class NpcCreation(val encounterId: EncounterId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/encounters/{encounterId}/new-npc"
            fun fromEntry(entry: NavBackStackEntry) = NpcCreation(
                EncounterId(
                    PartyId.fromString(entry.stringArgument("partyId")),
                    UUID.fromString(entry.stringArgument("encounterId")),
                ),
            )
        }

        override fun toString() =
            "parties/${encounterId.partyId}/encounters/${encounterId.encounterId}/new-npc"
    }

    @Immutable
    data class Compendium(val partyId: PartyId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/compendium"
            fun fromEntry(entry: NavBackStackEntry) =
                Compendium(PartyId.fromString(entry.stringArgument("partyId")))
        }

        override fun toString() = "parties/$partyId/compendium"
    }

    @Immutable
    data class CompendiumImport(val partyId: PartyId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/compendium-import"
            fun fromEntry(entry: NavBackStackEntry) = CompendiumImport(
                PartyId.fromString(entry.stringArgument("partyId"))
            )
        }

        override fun toString() = "parties/$partyId/compendium-import"
    }

    object InvitationScanner : Route() {
        override fun toString() = "invitation-scanner"
    }

    data class InvitationLink(val invitationJson: String) : Route() {
        companion object {
            private const val DEEP_LINK_PATTERN =
                "https://dnd-master-58fca.web.app/app/invitation?invitation={invitationJson}"

            override fun toString() = "invitation-link?invitationJson={invitationJson}"

            fun deepLinks(): List<NavDeepLink> = listOf(
                navDeepLink { uriPattern = DEEP_LINK_PATTERN }
            )

            fun fromEntry(entry: NavBackStackEntry) = InvitationLink(
                entry.arguments?.getString("invitationJson") ?: ""
            )
        }

        fun toDeepLink(): Uri =
            DEEP_LINK_PATTERN.replace("{invitationJson}", invitationJson).toUri()

        override fun toString(): String = "invitation-link?invitationJson=$invitationJson"
    }

    @Immutable
    data class ActiveCombat(val partyId: PartyId) : Route() {
        companion object {
            override fun toString() = "parties/{partyId}/combat"
            fun fromEntry(entry: NavBackStackEntry) = ActiveCombat(
                PartyId.fromString(entry.stringArgument("partyId"))
            )
        }

        override fun toString() = "parties/$partyId/combat"
    }
}

private fun NavBackStackEntry.stringArgument(name: String): String {
    return arguments?.getString(name) ?: error("Required argument '$name' not passed")
}
