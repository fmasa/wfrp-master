package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

interface Filter {
    fun process(value: String): String
}

class MaxLength(private val maxLength: Int) : Filter {
    override fun process(value: String) = if (value.length <= maxLength)
        value
    else value.substring(0, maxLength)
}

private typealias ValidationRule = Pair<(value: String) -> Boolean, String>

class Rules(private vararg val rules: ValidationRule) {
    companion object {
        val NoRules = Rules()

        @Composable
        fun NotBlank(): ValidationRule =
                { v: String -> v.isNotBlank() } to stringResource(R.string.error_cannot_be_empty)
    }

    /**
     * Returns error message if text value is invalid or null if it's valid
     */
    fun errorMessage(value: String): String?
        = rules.firstOrNull { (validator, _) -> ! validator(value) }?.second
}

@Composable
fun TextInput(
    label: String? = null,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    validate: Boolean,
    maxLength: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    rules: Rules = Rules.NoRules
) {
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.15f)

    Column(modifier) {
        label?.let {
            Text(
                label,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        val filters = arrayOf(MaxLength(maxLength))

        val errorMessage = if (validate) rules.errorMessage(value) else null
        /**
         * We cannot simply set background to OutlinedTextField, because it has 8 dp top padding
         * space for label. So we have to get our hands dirty by calculating height of OutlinedTextField
         * and positioning our background surface behind it.
         */
        val density = DensityAmbient.current

        Layout(
            children = {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()
                ) { }
                OutlinedTextField(
                    value = value,
                    keyboardType = keyboardType,
                    onValueChange = {
                        val filteredValue =
                            filters.fold(it) { value, filter -> filter.process(value) }

                        if (filteredValue != value) {
                            onValueChange(filteredValue)
                        }
                    },
                    textStyle = currentTextStyle().copy(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier.fillMaxWidth(),
                    inactiveColor = borderColor,
                    isErrorValue = errorMessage != null,
                )
            }
        ) { measurables, constraints ->
            val background = measurables[0]
            val textField = measurables[1]

            val textFieldTopPadding = with(density) { 8.dp.toIntPx() }
            val textFieldPlaceable = textField.measure(constraints)

            val height = textFieldPlaceable.height - textFieldTopPadding

            layout(constraints.maxWidth, height) {
                background.measure(constraints.copy(minHeight = height)).place(0, 0)
                textFieldPlaceable.place(0, -textFieldTopPadding)
            }
        }

        errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
