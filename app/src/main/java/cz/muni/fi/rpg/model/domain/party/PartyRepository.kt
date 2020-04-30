package cz.muni.fi.rpg.model.domain.party

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.common.ViewHolderFactory
import java.util.*

interface PartyRepository {
    suspend fun save(party: Party)

    /**
     * @throws PartyNotFound
     */
    suspend fun get(id: UUID): Party

    fun getLive(id: UUID): LiveData<Either<PartyNotFound, Party>>

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun forUser(
        userId: String,
        lifecycleOwner: LifecycleOwner,
        viewHolderFactory: ViewHolderFactory<Party>
    ): RecyclerView.Adapter<ViewHolder<Party>>
}