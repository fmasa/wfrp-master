package cz.muni.fi.rpg.model.firestore

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.common.ViewHolderFactory
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.infrastructure.GsonSnapshotParser
import cz.muni.fi.rpg.ui.partyList.adapter.FirestoreRecyclerAdapter
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirestorePartyRepository @Inject constructor(
    private val gson: Gson,
    firestore: FirebaseFirestore
) : PartyRepository {
    private val parties = firestore.collection("parties");
    private val parser = GsonSnapshotParser(Party::class, gson);

    override suspend fun save(party: Party) {
        parties.document(party.id.toString()).set(
            gson.fromJson(gson.toJson(party), Map::class.java),
            SetOptions.merge()
        ).await();
    }

    override suspend fun get(id: UUID): Party {
        try {
            val party = parties.document(id.toString()).get().await();
            return this.parser.parseSnapshot(party);
        } catch (e: FirebaseFirestoreException) {
            throw PartyNotFound(id, e)
        }
    }

    override fun forUser(
        userId: String,
        viewHolderFactory: ViewHolderFactory<Party>
    ): RecyclerView.Adapter<ViewHolder<Party>> {
        val options = FirestoreRecyclerOptions.Builder<Party>()
            .setQuery(parties.whereArrayContains("users", userId), parser).build()

        val adapter = FirestoreRecyclerAdapter(options, viewHolderFactory);

        adapter.startListening()

        return adapter
    }
}