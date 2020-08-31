package cz.muni.fi.rpg.ui.gameMaster.encounters.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.encounter.Encounter

class EncounterHolder(
    private val view: View,
    private val onClickListener: EntityListener<Encounter>
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Encounter) {
        view.findViewById<TextView>(R.id.title).text = item.name
        view.findViewById<View>(R.id.encounterCard).setOnClickListener { onClickListener(item) }
    }
}