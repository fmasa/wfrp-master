package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
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

        if (! item.alive) {
            view.combatantIcon.setImageResource(R.drawable.ic_dead)
            view.combatantIcon.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(view.context, R.color.colorGray)
            )
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