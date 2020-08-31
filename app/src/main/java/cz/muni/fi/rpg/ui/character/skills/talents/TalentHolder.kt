package cz.muni.fi.rpg.ui.character.skills.talents

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.talents.Talent

class TalentHolder(
    private val view: View,
    private val onClickListener: EntityListener<Talent>,
    private val onRemoveListener: EntityListener<Talent>
) : RecyclerView.ViewHolder(view) {
    fun bind(talent: Talent) {
        val description = view.findViewById<TextView>(R.id.talentItemDescription)

        view.findViewById<TextView>(R.id.talentItemTitle).text = talent.name
        description.text = talent.description
        view.findViewById<TextView>(R.id.takenValue).text = talent.taken.toString()

        if (talent.description.isBlank()) {
            description.visibility = View.GONE

            val talentItemLayout = view.findViewById<ConstraintLayout>(R.id.talentItemLayout)
            ConstraintSet().apply {
                clone(talentItemLayout)
                connect(
                    R.id.talentItemTitle,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(talentItemLayout)
            }
        }

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(talent)

                    false
                }
        }
        view.setOnClickListener { onClickListener(talent) }
    }
}