package cz.muni.fi.rpg.partyList.adapter

import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import cz.muni.fi.rpg.common.ViewHolder

class FirestoreRecyclerAdapter<TEntity, VH : ViewHolder<TEntity>>(
    options: FirestoreRecyclerOptions<TEntity>,
    private val viewHolderFactory: (parent: ViewGroup) -> VH
) : FirestoreRecyclerAdapter<TEntity, VH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewHolderFactory(parent);
    override fun onBindViewHolder(holder: VH, pos: Int, model: TEntity) = holder.bind(model)
}