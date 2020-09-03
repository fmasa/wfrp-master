package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import cz.muni.fi.rpg.R

class CharacterPoint(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var _value: Int = 0

    private var label: String? = null

    var value: Int
        get() = _value
        set(newValue) {
            _value = newValue
            findViewById<TextView>(R.id.characterPointValue).text = newValue.toString()
        }

    constructor(context: Context, label: String, value: Int): this(context, null) {
        this.label = label
        this.value = value
    }

    init {
        inflate(getContext(), R.layout.view_character_point, this)

        R.color.colorAccent
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CharacterPoint)

        (attributes.getString(R.styleable.CharacterPoint_name) ?: label)
            ?.let { name -> findViewById<TextView>(R.id.label).text = name }

        attributes.recycle()
    }

    fun setLabel(label: String) {
        this.label = label
        findViewById<TextView>(R.id.label).text = label
    }

    fun setColor(@ColorRes color: Int) {
        findViewById<TextView>(R.id.characterPointValue).setTextColor(ContextCompat.getColor(context, color))
    }

    fun setIncrementListener(listener: () -> Unit) {
        findViewById<ImageButton>(R.id.characterPointUp).setOnClickListener { listener() }
    }

    fun setDecrementListener(listener: () -> Unit) {
        findViewById<ImageButton>(R.id.characterPointDown).setOnClickListener { listener() }
    }
}
