package cz.frantisekmasa.wfrp_master.common.compendium.import

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import kotlinx.serialization.Serializable

@Serializable
data class JournalEntryImport(
    val name: String,
    val text: String,
    val gmText: String,
    val isPinned: Boolean,
    val parents: List<String>,
    val isVisibleToPlayers: Boolean,
) {
    fun toJournalEntry() =
        JournalEntry(
            id = uuid4(),
            name = name,
            text = text,
            gmText = gmText,
            isPinned = isPinned,
            parents = parents,
            isVisibleToPlayers = isVisibleToPlayers,
        )

    companion object {
        fun fromJournalEntry(journalEntry: JournalEntry): JournalEntryImport {
            return JournalEntryImport(
                name = journalEntry.name,
                text = journalEntry.text,
                gmText = journalEntry.gmText,
                isPinned = journalEntry.isPinned,
                parents = journalEntry.parents,
                isVisibleToPlayers = journalEntry.isVisibleToPlayers,
            )
        }
    }
}
