package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import kotlinx.android.synthetic.main.encounter_item.view.*

class EncounterHolder(
    private val view: View,
    private val onClickListener: EntityListener<Encounter>
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Encounter) {
        view.title.text = item.name
        view.encounterCard.setOnClickListener { onClickListener(item) }
    }
}