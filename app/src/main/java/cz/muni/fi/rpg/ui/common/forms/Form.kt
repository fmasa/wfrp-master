package cz.muni.fi.rpg.ui.common.forms

import android.view.View
import androidx.annotation.IdRes
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.ui.views.TextInput

class Form() {
    /**
     * Layout ID resource to Input object
     */
    private var inputs: MutableMap<Int, Input> = mutableMapOf()

    fun addTextInput(view: TextInput): Input {
        val input = Input(view.getTextInputLayout())

        addInputUnderId(view.id, input)

        return input
    }

    fun addTextInput(view: TextInputLayout): Input {
        val input = Input(view)

        addInputUnderId(view.id, input)

        return input
    }

    fun getInput(@IdRes viewId: Int): Input {
        return inputs[viewId]
            ?: throw IndexOutOfBoundsException("Input with id $viewId was not found")
    }

    /**
     * Validate all inputs
     * Returns true if all inputs are valid, false otherwise
     */
    fun validate(): Boolean = !inputs.values.map { it.validate() }.contains(false)

    private fun addInputUnderId(@IdRes id: Int, input: Input) {
        require(id != View.NO_ID) { "Input view must have ID" }
        require(!inputs.containsKey(id)) { "Form already contains input with ID $id" }

        inputs[id] = input
    }
}