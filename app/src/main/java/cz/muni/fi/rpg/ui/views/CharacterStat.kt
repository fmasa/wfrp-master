package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.view_character_stat.view.*

class CharacterStat(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var _value: Int = 0

    var value: Int
        get() = _value
        set(newValue) {
            _value = newValue
            characterPointValue.text = newValue.toString()
        }

    init {
        inflate(getContext(), R.layout.view_character_stat, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CharacterStat)

        attributes.getString(R.styleable.CharacterStat_statName)?.let { name -> label.text = name }

        attributes.recycle()
    }
}
