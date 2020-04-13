package cz.muni.fi.rpg.model.domain.party

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.OnClickListener
import cz.muni.fi.rpg.common.ViewHolder
import java.util.*

interface PartyRepository {
    suspend fun save(party: Party)

    suspend fun get(id: UUID): Party;

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun <VH : ViewHolder<Party>> forUser(
        userId: String,
        viewHolderFactory: (parent: ViewGroup) -> VH,
        onClickListener: OnClickListener<Party>
    ): RecyclerView.Adapter<VH>
}