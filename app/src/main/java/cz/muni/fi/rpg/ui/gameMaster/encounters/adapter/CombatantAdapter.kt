package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.encounter.Combatant

class CombatantAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Combatant>,
    private val onRemoveListener: EntityListener<Combatant>
) : ListAdapter<Combatant, CombatantHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<Combatant>() {
        override fun areItemsTheSame(oldItem: Combatant, newItem: Combatant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Combatant, newItem: Combatant): Boolean {
            return oldItem.name == newItem.name && oldItem.note == newItem.note
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CombatantHolder {
        return CombatantHolder(
            layoutInflater.inflate(R.layout.combatant_item, parent, false),
            onClickListener,
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: CombatantHolder, position: Int) {
        holder.bind(getItem(position))
    }
}