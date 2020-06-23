package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.os.Parcelable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.view_text_input.view.*

class TextInput(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    @Parcelize
    private data class SavedState(val value: String, val superState: Parcelable?) : Parcelable

    private var value: String = ""

    private val textInputLayout: TextInputLayout

    init {
        isSaveEnabled = true

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

        textInputLayout.editText?.let { editText ->
            if (attributes.hasValue(R.styleable.TextInput_android_inputType)) {
                editText.inputType = attributes.getIntegerOrThrow(R.styleable.TextInput_android_inputType)
            }

            editText.filters = filters.toTypedArray()
            editText.setText(value)
            editText.addTextChangedListener { value = it.toString().trim() }
        }

        attributes.recycle()
    }

    fun getTextInputLayout(): TextInputLayout = textInputLayout

    fun setDefaultValue(value: String) {
        if (value == "") {
            textInputLayout.editText?.setText(value)
        }
    }

    fun getValue(): String = value

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(value, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        check(state is SavedState)

        value = state.value
        textInputLayout.editText?.setText(value)

        super.onRestoreInstanceState(state.superState)
    }
}
