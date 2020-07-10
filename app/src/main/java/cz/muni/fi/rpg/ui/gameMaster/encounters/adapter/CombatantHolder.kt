package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.encounter.Combatant
import kotlinx.android.synthetic.main.combatant_item.view.*

class CombatantHolder(
    private val view: View,
    private val onClickListener: EntityListener<Combatant>,
    private val onRemoveListener: EntityListener<Combatant>
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Combatant) {
        view.combatantName.text = item.name
        view.combatantNote.text = item.note


        if (item.note.isBlank()) {
            view.combatantNote.visibility = View.GONE

            ConstraintSet().apply {
                clone(view.combatantLayout)
                connect(
                    R.id.inventoryItemName,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(view.combatantLayout)
            }
        }

        view.setOnClickListener { onClickListener(item) }

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(item)

                    false
                }
        }
    }
}