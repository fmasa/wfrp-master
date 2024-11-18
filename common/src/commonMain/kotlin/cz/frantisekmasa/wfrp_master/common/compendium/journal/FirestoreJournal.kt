package cz.frantisekmasa.wfrp_master.common.compendium.journal

import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import dev.gitlive.firebase.firestore.Filter
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreJournal(
    private val firestore: FirebaseFirestore,
) : Journal {
    override fun findByFolders(
        partyId: PartyId,
        folders: List<String>,
    ): Flow<List<JournalEntry>> {
        val parents =
            folders.map { folder ->
                folder
                    .split(JournalEntry.PARENT_SEPARATOR)
                    .map { it.trim() }
            }

        return firestore.collection(Schema.PARTIES)
            .document(partyId.toString())
            .collection(Schema.Compendium.JOURNAL)
            .where {
                parents.map<List<String>, Filter> { ("parents" equalTo it) }
                    .reduce { a, b -> a or b }
            }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(JournalEntry.serializer()) }
            }
    }
}
