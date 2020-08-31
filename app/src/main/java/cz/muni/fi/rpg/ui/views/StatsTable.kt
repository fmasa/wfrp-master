package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats

class StatsTable : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_stats_table, this)
    }

    fun setValue(stats: Stats) {
        findViewById<CharacterStat>(R.id.weaponSkill).value = stats.weaponSkill
        findViewById<CharacterStat>(R.id.ballisticSkill).value = stats.ballisticSkill
        findViewById<CharacterStat>(R.id.strength).value = stats.strength
        findViewById<CharacterStat>(R.id.toughness).value = stats.toughness
        findViewById<CharacterStat>(R.id.agility).value = stats.agility
        findViewById<CharacterStat>(R.id.intelligence).value = stats.intelligence
        findViewById<CharacterStat>(R.id.willPower).value = stats.willPower
        findViewById<CharacterStat>(R.id.fellowship).value = stats.fellowship
        findViewById<CharacterStat>(R.id.initiative).value = stats.initiative
        findViewById<CharacterStat>(R.id.dexterity).value = stats.dexterity
    }
}