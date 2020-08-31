package cz.muni.fi.rpg.ui.character.skills.talents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.DiffCallback

class TalentAdapter(
    private val layoutInflater: LayoutInflater,
    private val onClickListener: EntityListener<Talent>,
    private val onRemoveListener: EntityListener<Talent>
) : ListAdapter<Talent, TalentHolder>(DiffCallback({a, b -> a.id == b.id}, {a, b -> a == b})) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalentHolder {
        return TalentHolder(
            layoutInflater.inflate(R.layout.talent_item, parent, false),
            onClickListener,
            onRemoveListener
        )
    }

    override fun onBindViewHolder(holder: TalentHolder, position: Int) {
        holder.bind(getItem(position))
    }
}