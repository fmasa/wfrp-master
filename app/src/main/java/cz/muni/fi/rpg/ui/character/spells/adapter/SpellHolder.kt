package cz.muni.fi.rpg.ui.character.spells.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.spells.Spell

class SpellHolder(
    private val view: View,
    private val onClickListener: EntityListener<Spell>,
    private val onRemoveListener: EntityListener<Spell>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: Spell) {
        val spellEffect = view.findViewById<TextView>(R.id.spellEffect)
        view.findViewById<TextView>(R.id.spellName).text = item.name
        spellEffect.text = item.effect

        view.findViewById<TextView>(R.id.castingNumber).text = item.castingNumber.toString()

        if (item.effect.isBlank()) {
            spellEffect.visibility = View.GONE

            val spellLayout = view.findViewById<ConstraintLayout>(R.id.spellLayout)
            ConstraintSet().apply {
                clone(spellLayout)
                connect(
                    R.id.spellName,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(spellLayout)
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