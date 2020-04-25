package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.view_character_point.view.*

class CharacterPoint(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var _value: Int = 0

    var value: Int
        get() = _value
        set(newValue) {
            _value = newValue
            characterPointValue.text = newValue.toString()
        }

    init {
        inflate(getContext(), R.layout.view_character_point, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CharacterPoint)

        attributes.getString(R.styleable.CharacterPoint_name)?.let { name -> label.text = name }

        attributes.recycle()
    }

    fun setIncrementListener(listener: () -> Unit) {
        characterPointUp.setOnClickListener { listener() }
    }

    fun setDecrementListener(listener: () -> Unit) {
        characterPointDown.setOnClickListener { listener() }
    }
}
