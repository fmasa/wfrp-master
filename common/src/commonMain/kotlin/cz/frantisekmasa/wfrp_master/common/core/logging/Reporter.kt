package cz.frantisekmasa.wfrp_master.common.core.logging

import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import java.util.UUID

expect object Reporter {
    fun setUserId(id: UserId)

    fun log(message: String)

    fun recordThrowable(throwable: Throwable)

    fun recordEvent(
        name: String,
        parameters: Map<String, String>,
    )
}

/**
 * This object is added, because expect/actual classes are not autocompleted in the IDE.
 */
object Reporting {
    object Dimension {
        const val CHARACTER_TYPE = "character_type"
        const val ITEM_TYPE = "item_type"
        const val IMPORT_SOURCE = "source"
        const val CONTEXT = "context"
        const val LANGUAGE = "language"
    }

    private val reporting = EventReporting()

    fun setUserId(id: UserId) = Reporter.setUserId(id)

    fun record(block: EventReporting.() -> Unit) {
        reporting.block()
    }

    fun recordThrowable(throwable: Throwable) = Reporter.recordThrowable(throwable)

    fun log(message: String) = Reporter.log(message)
}

private fun recordEvent(
    name: String,
    parameters: Map<String, String>,
) {
    Reporter.recordEvent(name, parameters)
}

class EventReporting() {
    fun appLanguageChanged(language: String) {
        recordEvent(
            "app_language_changed",
            mapOf(Reporting.Dimension.LANGUAGE to language),
        )
    }

    fun journalOpened(context: String) {
        recordEvent("journal_opened", mapOf(Reporting.Dimension.CONTEXT to context))
    }

    fun partyCreated(partyId: PartyId) {
        recordEvent("create_party", mapOf("party_id" to partyId.toString()))
    }

    fun characterCreated(
        characterId: CharacterId,
        encounterId: EncounterId?,
        type: CharacterType,
    ) {
        recordEvent(
            "create_character",
            mapOf(
                "party_id" to characterId.partyId.toString(),
                "character_id" to characterId.id,
                "encounter_id" to (encounterId?.toString() ?: ""),
                Reporting.Dimension.CHARACTER_TYPE to type.name,
                Reporting.Dimension.CONTEXT to
                    if (encounterId != null) {
                        "encounter"
                    } else {
                        "party"
                    },
            ),
        )
    }

    fun encounterCreated(
        encounterId: UUID,
        partyId: PartyId,
    ) {
        recordEvent(
            "create_encounter",
            mapOf(
                "encounterId" to encounterId.toString(),
                "partyId" to partyId.toString(),
            ),
        )
    }

    fun characterItemAdded(itemType: String) {
        recordEvent(
            "character_item_added",
            mapOf(Reporting.Dimension.ITEM_TYPE to itemType),
        )
    }

    fun trappingAddedToContainer() {
        recordEvent("trapping_added_to_container", emptyMap())
    }

    fun trappingRemovedFromContainer() {
        recordEvent("trapping_removed_from_container", emptyMap())
    }

    fun basicSkillsAdded() {
        recordEvent("basic_skills_added", emptyMap())
    }

    fun characterItemDuplicated(itemType: String) {
        recordEvent(
            "character_item_duplicated",
            mapOf(Reporting.Dimension.ITEM_TYPE to itemType),
        )
    }

    fun conditionsChanged() {
        recordEvent("conditions_changed", emptyMap())
    }

    fun invitationLinkShared(partyId: PartyId) {
        recordEvent(
            "share",
            mapOf(
                "content_type" to "party_invitation",
                "item_id" to partyId.toString(),
                "method" to "link",
            ),
        )
    }

    fun combatStarted(partyId: PartyId) {
        recordEvent(
            "combat_started",
            mapOf("party_id" to partyId.toString()),
        )
    }

    fun partyLanguageChanged(
        partyId: PartyId,
        language: String,
    ) {
        recordEvent(
            "party_language_changed",
            mapOf(
                "party_id" to partyId.toString(),
                "language" to language,
            ),
        )
    }

    fun partyAmbitionsChanged(partyId: PartyId) {
        recordEvent("party_ambitions_changed", mapOf("party_id" to partyId.toString()))
    }

    fun characterAmbitionsChanged(characterId: CharacterId) {
        recordEvent(
            "character_ambitions_changed",
            mapOf(
                "character_id" to characterId.id,
                "party_id" to characterId.partyId.toString(),
            ),
        )
    }

    fun experiencePointsUpdated() {
        recordEvent("experience_points_updated", emptyMap())
    }

    fun avatarChanged() {
        recordEvent("avatar_changed", emptyMap())
    }

    fun avatarRemoved() {
        recordEvent("avatar_removed", emptyMap())
    }

    fun drawerItemClicked(itemType: String) {
        recordEvent(
            "drawer_item_clicked",
            mapOf(Reporting.Dimension.ITEM_TYPE to itemType),
        )
    }

    fun characterTurnedIntoNPC() {
        recordEvent("character_turned_into_npc", emptyMap())
    }

    fun characterUnlinkedFromUser() {
        recordEvent("character_unlinked_from_user", emptyMap())
    }

    fun rulebookImportClicked(source: String) {
        recordEvent(
            "rulebook_import_clicked",
            mapOf(Reporting.Dimension.IMPORT_SOURCE to source),
        )
    }

    fun compendiumImportFinished(source: String) {
        recordEvent(
            "compendium_import_finished",
            mapOf(Reporting.Dimension.IMPORT_SOURCE to source),
        )
    }

    fun joinedParty(partyId: PartyId) {
        // Using the Google Analytics recommended event
        recordEvent("join_group", mapOf("group_id" to partyId.toString()))
    }
}
