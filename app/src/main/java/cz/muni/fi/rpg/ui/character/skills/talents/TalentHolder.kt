package cz.muni.fi.rpg.ui.character.skills.talents

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.talents.Talent
import kotlinx.android.synthetic.main.talent_item.view.*

class TalentHolder(
    private val view: View,
    private val onClickListener: EntityListener<Talent>,
    private val onRemoveListener: EntityListener<Talent>
) : RecyclerView.ViewHolder(view) {
    fun bind(talent: Talent) {
        view.talentItemTitle.text = talent.name;
        view.talentItemDescription.text = talent.description
        view.takenValue.text = talent.taken.toString()

        if (talent.description.isBlank()) {
            view.talentItemDescription.visibility = View.GONE

            ConstraintSet().apply {
                clone(view.talentItemLayout)
                connect(
                    R.id.talentItemTitle,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                applyTo(view.talentItemLayout)
            }
        }

        view.setOnCreateContextMenuListener { menu, v, _ ->
            menu.add(0, v.id, 0, R.string.remove)
                .setOnMenuItemClickListener {
                    onRemoveListener(talent)

                    false
                }
        }
        view.setOnClickListener { onClickListener(talent) };
    }
}