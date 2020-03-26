package cz.muni.fi.rpg.model.firestore

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.model.Party
import cz.muni.fi.rpg.model.PartyRepository
import cz.muni.fi.rpg.model.infrastructure.GsonSnapshotParser
import cz.muni.fi.rpg.model.infrastructure.UUIDAdapter
import cz.muni.fi.rpg.partyList.adapter.FirestoreRecyclerAdapter
import java.util.*

class FirestorePartyRepository : PartyRepository {
    private val parties = Firebase.firestore.collection("parties");
    private val gson = GsonBuilder()
        .registerTypeAdapter(UUID::class.java, UUIDAdapter())
        .create()

    override fun save(party: Party): Task<Void> {
        return parties.document(party.id.toString()).set(
            gson.fromJson(gson.toJson(party), Map::class.java),
            SetOptions.merge()
        );
    }

    override fun <VH : ViewHolder<Party>> forUser(
        userId: String,
        viewHolderFactory: (parent: ViewGroup) -> VH
    ) : RecyclerView.Adapter<VH> {
        val options = FirestoreRecyclerOptions.Builder<Party>()
            .setQuery(
                parties.whereArrayContains("users", userId),
                GsonSnapshotParser(Party::class.java)
            ).build()

        val adapter = FirestoreRecyclerAdapter(options, viewHolderFactory);

        adapter.startListening()

        return adapter
    }
}