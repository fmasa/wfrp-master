package cz.muni.fi.rpg.ui.character.spells.adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.spells.Spell
import kotlinx.android.synthetic.main.spell_item.view.*

class SpellHolder(
    private val view: View,
    private val onClickListener: EntityListener<Spell>,
    private val onRemoveListener: EntityListener<Spell>
) : RecyclerView.ViewHolder(view) {
    fun bind(item: Spell) {
        view.spellName.text = item.name
        view.spellEffect.text = item.effect

        view.castingNumber.text = item.castingNumber.toString()

        if (item.effect.isBlank()) {
            view.spellEffect.visibility = View.GONE

            ConstraintSet().apply {
                clone(view.spellLayout)
                connect(
                    R.id.spellName,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(view.spellLayout)
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