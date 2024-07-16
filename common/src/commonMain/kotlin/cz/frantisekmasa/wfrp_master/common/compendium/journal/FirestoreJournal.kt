package cz.frantisekmasa.wfrp_master.common.compendium.journal

import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreJournal(
    private val firestore: FirebaseFirestore,
) : Journal {
    override fun findByParents(
        partyId: PartyId,
        parents: List<String>,
    ): Flow<List<JournalEntry>> {
        return firestore.collection(Schema.PARTIES)
            .document(partyId.toString())
            .collection(Schema.Compendium.JOURNAL)
            .where { "parents" equalTo parents }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(JournalEntry.serializer()) }
            }
    }

    override fun findByFolder(
        partyId: PartyId,
        folder: String,
    ): Flow<List<JournalEntry>> {
        return findByParents(
            partyId,
            folder.split(JournalEntry.PARENT_SEPARATOR)
                .map { it.trim() },
        )
    }
}
