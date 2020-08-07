package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats
import kotlinx.android.synthetic.main.view_stats_table.view.*

class StatsTable : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_stats_table, this)
    }

    fun setValue(stats: Stats) {
        weaponSkill.value = stats.weaponSkill
        ballisticSkill.value = stats.ballisticSkill
        strength.value = stats.strength
        toughness.value = stats.toughness
        agility.value = stats.agility
        intelligence.value = stats.intelligence
        willPower.value = stats.willPower
        fellowship.value = stats.fellowship
        initiative.value = stats.initiative
        dexterity.value = stats.dexterity
    }
}