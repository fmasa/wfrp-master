package cz.muni.fi.rpg.model.firestore

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.model.Party
import cz.muni.fi.rpg.model.PartyRepository
import cz.muni.fi.rpg.model.infrastructure.GsonSnapshotParser
import cz.muni.fi.rpg.model.infrastructure.UUIDAdapter
import cz.muni.fi.rpg.partyList.adapter.FirestoreRecyclerAdapter
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestorePartyRepository : PartyRepository {
    private val parties = Firebase.firestore.collection("parties");
    private val gson = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .create()
    private val parser = GsonSnapshotParser(Party::class.java, gson);


    override suspend fun save(party: Party) {
        parties.document(party.id.toString()).set(
            gson.fromJson(gson.toJson(party), Map::class.java),
            SetOptions.merge()
        ).await();
    }

    override suspend fun get(id: UUID): Party {
        val party = parties.document(id.toString()).get().await();

        return this.parser.parseSnapshot(party);
    }

    override fun <VH : ViewHolder<Party>> forUser(
        userId: String,
        viewHolderFactory: (parent: ViewGroup) -> VH,
        onClickListener: OnClickListener<Party>
    ) : RecyclerView.Adapter<VH> {
        val options = FirestoreRecyclerOptions.Builder<Party>()
            .setQuery(parties.whereArrayContains("users", userId), parser).build()

        val adapter = FirestoreRecyclerAdapter(options, viewHolderFactory, onClickListener);

        adapter.startListening()

        return adapter
    }
}