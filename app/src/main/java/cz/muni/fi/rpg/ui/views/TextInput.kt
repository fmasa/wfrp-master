package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.getIntegerOrThrow
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.view_text_input.view.*

class TextInput(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val textInputLayout: TextInputLayout

    init {
        val view = inflate(context, R.layout.view_text_input, this)
        textInputLayout = view.textInputLayout

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TextInput)

        val label = attributes.getString(R.styleable.TextInput_inputLabel)
        if (label != null) {
            view.label.text = label
        } else {
            view.label.visibility = View.GONE
        }

        val filters = mutableListOf<InputFilter>()

        if (attributes.hasValue(R.styleable.TextInput_android_maxLength)) {
            filters.add(
                InputFilter.LengthFilter(
                    attributes.getIntegerOrThrow(R.styleable.TextInput_android_maxLength)
                )
            )
        }

        if (attributes.hasValue(R.styleable.TextInput_android_inputType)) {
            textInputLayout.editText?.inputType =
                attributes.getIntegerOrThrow(R.styleable.TextInput_android_inputType)
        }

        attributes.recycle()

        textInputLayout.editText?.filters = filters.toTypedArray()
    }

    fun getTextInputLayout(): TextInputLayout = textInputLayout

    fun setDefaultValue(value: String) {
        textInputLayout.editText?.setText(value)
    }

    fun getValue(): String = requireNotNull(textInputLayout.editText).text.toString()
}
