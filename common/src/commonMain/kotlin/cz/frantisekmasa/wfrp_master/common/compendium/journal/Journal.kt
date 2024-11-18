package cz.frantisekmasa.wfrp_master.common.compendium.journal

import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow

interface Journal {
    fun findByFolders(
        partyId: PartyId,
        folders: List<String>,
    ): Flow<List<JournalEntry>>

    fun findByFolder(
        partyId: PartyId,
        folder: String,
    ): Flow<List<JournalEntry>> = findByFolders(partyId, listOf(folder))
}
