package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.view_text_input.view.*

class TextInput(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val textInputLayout: TextInputLayout

    init {
        val view = inflate(context, R.layout.view_text_input, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TextInput)

        attributes.getString(R.styleable.TextInput_inputLabel)
            ?.let { name -> view.label.text = name }

        attributes.recycle()

        textInputLayout = view.textInputLayout
    }

    fun getTextInputLayout(): TextInputLayout = textInputLayout

    fun setDefaultValue(value: String) {
        textInputLayout.editText?.setText(value)
    }

    fun getValue(): String = requireNotNull(textInputLayout.editText).text.toString()
}
