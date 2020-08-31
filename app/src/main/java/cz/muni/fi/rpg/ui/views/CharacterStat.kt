package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import cz.muni.fi.rpg.R

class CharacterStat(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var _value: Int = 0

    var value: Int
        get() = _value
        set(newValue) {
            _value = newValue
            findViewById<TextView>(R.id.characterPointValue).text = newValue.toString()
        }

    init {
        inflate(getContext(), R.layout.view_character_stat, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CharacterStat)

        attributes.getString(R.styleable.CharacterStat_statName)
            ?.let { name -> findViewById<TextView>(R.id.label).text = name }

        attributes.recycle()
    }
}
