package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.encounter.Encounter

class EncounterAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Encounter>
) : ListAdapter<Encounter, EncounterHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<Encounter>() {
        override fun areItemsTheSame(oldItem: Encounter, newItem: Encounter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Encounter, newItem: Encounter): Boolean {
            return oldItem.name == newItem.name;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncounterHolder {
        return EncounterHolder(
            layoutInflater.inflate(R.layout.encounter_item, parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: EncounterHolder, position: Int) {
        holder.bind(getItem(position))
    }
}