package cz.muni.fi.rpg.model.domain.party

import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.common.ViewHolderFactory
import java.util.*

interface PartyRepository {
    suspend fun save(party: Party)

    /**
     * @throws PartyNotFound
     */
    suspend fun get(id: UUID): Party

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun forUser(
        userId: String,
        viewHolderFactory: ViewHolderFactory<Party>
    ): RecyclerView.Adapter<ViewHolder<Party>>
}