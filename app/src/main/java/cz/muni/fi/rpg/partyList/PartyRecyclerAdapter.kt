package cz.muni.fi.rpg.partyList

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.Party

class PartyRecyclerAdapter(db: FirebaseFirestore, userId: String) : FirestoreRecyclerAdapter<Party, PartyHolder>(
    FirestoreRecyclerOptions.Builder<Party>()
    .setQuery(
        db.collection("parties").whereArrayContains("users", userId),
        Party::class.java
    ).build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.party_item, parent, false)

        return PartyHolder(view)
    }

    override fun onBindViewHolder(holder: PartyHolder, pos: Int, model: Party) = holder.bind(model)
}