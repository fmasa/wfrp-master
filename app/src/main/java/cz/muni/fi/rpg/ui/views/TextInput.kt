package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.os.Parcelable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.R
import kotlinx.android.parcel.Parcelize

class TextInput(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    @Parcelize
    private data class SavedState(
        val value: String,
        val emptyValue: String,
        val superState: Parcelable?
    ) : Parcelable

    private var value: String = ""
    private var emptyValue: String = ""

    private val textInputLayout: TextInputLayout

    init {
        isSaveEnabled = true

        val view = inflate(context, R.layout.view_text_input, this)
        textInputLayout = view.findViewById(R.id.textInputLayout)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TextInput)

        val labelView = view.findViewById<TextView>(R.id.label)
        val label = attributes.getString(R.styleable.TextInput_inputLabel)


        if (label != null) {
            labelView.text = label
        } else {
            labelView.visibility = View.GONE
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


            if (attributes.hasValue(R.styleable.TextInput_android_minLines)) {
                editText.minLines = attributes.getIntegerOrThrow(R.styleable.TextInput_android_minLines)
            }

            if (attributes.hasValue(R.styleable.TextInput_android_maxLines)) {
                editText.maxLines = attributes.getIntegerOrThrow(R.styleable.TextInput_android_maxLines)
            }

            editText.filters = filters.toTypedArray()
            editText.setText(value)
            editText.addTextChangedListener { value = it.toString().trim() }
        }

        attributes.recycle()
    }

    fun getTextInputLayout(): TextInputLayout = textInputLayout

    fun setDefaultValue(value: String, force: Boolean = false) {
        if (force || textInputLayout.editText?.text.toString() == "") {
            textInputLayout.editText?.setText(if (value == emptyValue) "" else value)
        }
    }

    fun getValue(): String = if (value == "") emptyValue else value

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(value, emptyValue, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        check(state is SavedState)

        value = state.value
        emptyValue = state.emptyValue
        textInputLayout.editText?.setText(value)

        super.onRestoreInstanceState(state.superState)
    }
}
