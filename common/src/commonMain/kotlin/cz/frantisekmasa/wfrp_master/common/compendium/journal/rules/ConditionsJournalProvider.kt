package cz.frantisekmasa.wfrp_master.common.compendium.journal.rules

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionsJournal
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry.Companion.PARENT_SEPARATOR
import cz.frantisekmasa.wfrp_master.common.compendium.journal.Journal
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Condition
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.localization.Translator
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConditionsJournalProvider(
    private val journal: Journal,
) {
    fun getConditionsJournal(
        partyId: PartyId,
        translator: Translator,
    ): Flow<ConditionsJournal> {
        val conditions =
            Condition.entries.associateWith { translator.translate(it.translatableName) }
        val folder = translator.translate(Str.journal_folder_conditions)

        return journal.findByFolder(partyId, folder)
            .map { entries ->
                val journalEntries = entries.associateBy { it.name.trim().lowercase() }

                ConditionsJournal(
                    conditions.map { (condition, conditionName) ->
                        val journalEntry = journalEntries[conditionName.lowercase()]
                        condition to
                            ConditionsJournal.Entry(
                                partyId = partyId,
                                journalEntryId = journalEntry?.id,
                                journalEntryName = "$folder $PARENT_SEPARATOR $conditionName",
                            )
                    }
                        .toMap()
                        .toImmutableMap(),
                )
            }
    }
}
