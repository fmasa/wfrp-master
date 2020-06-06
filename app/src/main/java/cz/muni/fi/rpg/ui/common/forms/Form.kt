package cz.muni.fi.rpg.ui.common.forms

import android.view.View
import com.google.android.material.textfield.TextInputLayout

class Form() {
    /**
     * Layout ID resource to Input object
     */
    private var inputs: MutableMap<Int, Input> = mutableMapOf()

    fun addTextInput(view: TextInputLayout): Input {
        require(view.id != View.NO_ID) { "Input view must have ID" }
        require(!inputs.containsKey(view.id)) { "Form already contains input with ID ${view.id}" }

        val input = Input(view)
        inputs[view.id] = input

        return input
    }

    /**
     * Validate all inputs
     * Returns true if all inputs are valid, false otherwise
     */
    fun validate(): Boolean = !inputs.values.map { it.validate() }.contains(false)
}