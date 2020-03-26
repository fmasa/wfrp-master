package cz.muni.fi.rpg.model

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import cz.muni.fi.rpg.common.ViewHolder

interface PartyRepository {
    fun save(party: Party): Task<Void>

    fun <VH : ViewHolder<Party>> forUser(
        userId: String,
        viewHolderFactory: (parent: ViewGroup) -> VH
    ): RecyclerView.Adapter<VH>
}