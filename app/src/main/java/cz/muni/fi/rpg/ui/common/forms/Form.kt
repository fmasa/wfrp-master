package cz.muni.fi.rpg.ui.common.forms

import com.google.android.material.textfield.TextInputLayout

class Form() {
    private var inputs: MutableList<Input> = mutableListOf()

    fun addTextInput(view: TextInputLayout): Input {
        val input = Input(view)

        inputs.add(input)

        return input
    }

    /**
     * Validate all inputs
     * Returns true if all inputs are valid, false otherwise
     */
    fun validate(): Boolean = ! inputs.map { it.validate() }.contains(false)
}