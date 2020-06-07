package cz.muni.fi.rpg.ui.common.forms

import android.text.Editable
import android.text.InputFilter
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

class Input(private val layout: TextInputLayout) {
    private val validators: MutableList<Validator> = mutableListOf()

    private var liveValidationRegistered = false

    /**
     * Limits maximum number of characters allowed in EditText
     */
    fun setMaxLength(maxLength: Int, showCounter: Boolean = true) {
        layout.apply {
            editText?.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            if (showCounter) {
                isCounterEnabled = true
                counterMaxLength = maxLength
            }
        }
    }

    /**
     * Adds predicate that must be true for input to be considered valid
     *
     * After input is validated for the first time
     */
    fun addLiveRule(errorMessage: String, rule: (Editable?) -> Boolean) {
        validators.add(Validator(errorMessage, rule, true))
    }

    fun setNotBlank(errorMessage: String) {
        addLiveRule(errorMessage) { !it.isNullOrBlank() }
    }

    fun validate(): Boolean {
        for (validator in validators) {
            if (!validator.rule(layout.editText?.text)) {
                layout.error = validator.errorMessage
                registerLiveValidationIfNecessary()

                return false
            }
        }

        layout.error = null

        return true
    }

    /**
     * Returns TRIMMED value of EditText
     */
    fun getValue() = layout.editText?.text.toString().trim()

    private fun registerLiveValidationIfNecessary() {
        if (liveValidationRegistered) {
            return
        }

        layout.editText?.addTextChangedListener {
            for (validator in validators.filter { it.isLiveValidationSupported }) {
                if (!validator.rule(layout.editText?.text)) {
                    layout.error = validator.errorMessage

                    return@addTextChangedListener
                }
            }
        }

        liveValidationRegistered = true
    }
}