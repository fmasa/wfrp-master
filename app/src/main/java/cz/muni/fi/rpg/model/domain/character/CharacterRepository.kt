package cz.muni.fi.rpg.model.domain.character

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.common.ViewHolderFactory
import java.util.UUID

interface CharacterRepository {
    suspend fun save(partyId: UUID, character: Character)

    /**
     * @throws CharacterNotFound
     */
    suspend fun get(partyId: UUID, userId: String): Character

    suspend fun hasCharacterInParty(userId: String, partyId: UUID) : Boolean

    fun inParty(
        partyId: UUID,
        lifecycleOwner: LifecycleOwner,
        viewHolderFactory: ViewHolderFactory<Character>
    ): RecyclerView.Adapter<ViewHolder<Character>>
}