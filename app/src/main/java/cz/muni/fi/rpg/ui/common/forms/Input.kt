package cz.muni.fi.rpg.ui.common.forms

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

class Input(private val layout: TextInputLayout, private val context: Context) {
    private val validators: MutableList<Validator> = mutableListOf()

    private var liveValidationRegistered = false

    private var showErrorInEditText = false

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

    /**
     * Adds predicate that must be true for input to be considered valid
     *
     * After input is validated for the first time
     */
    fun addLiveRule(@StringRes errorMessageId: Int, rule: (Editable?) -> Boolean) {
        addLiveRule(context.getString(errorMessageId), rule)
    }

    fun setNotBlank(errorMessage: String) {
        addLiveRule(errorMessage) { !it.isNullOrBlank() }
    }

    fun validate(): Boolean {
        for (validator in validators) {
            if (!validator.rule(layout.editText?.text)) {
                showError(validator.errorMessage)
                registerLiveValidationIfNecessary()

                return false
            }
        }

        layout.error = null

        return true
    }

    fun setDefaultValue(value: String) {
        layout.editText?.setText(value)
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
                    showError(validator.errorMessage)

                    return@addTextChangedListener
                }
            }
        }

        liveValidationRegistered = true
    }

    private fun showError(message: String) {
        if (showErrorInEditText) {
            layout.editText?.error = message
        } else {
            layout.error = message
        }
    }
}