package cz.frantisekmasa.wfrp_master.common.character.trappings.detail.journal

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournal
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItem

class NamedEnumJournalItem(
    val value: NamedEnum,
    val entry: TrappingJournal.Entry,
) : JournalItem {
    override val key = value
    override val journalEntryId = entry.journalEntryId
    override val journalEntryName = entry.journalEntryName
    override val partyId = entry.partyId

    @Composable
    override fun getName(): String {
        return value.localizedName
    }
}
