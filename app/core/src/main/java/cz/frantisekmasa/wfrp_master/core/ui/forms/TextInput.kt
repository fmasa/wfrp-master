package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.max

private interface Filter {
    fun process(value: String): String

    class MaxLength(private val maxLength: Int) : Filter {
        override fun process(value: String) = if (value.length <= maxLength)
            value
        else value.substring(0, maxLength)
    }

    object SingleLine : Filter {
        override fun process(value: String): String = value.replace("\n", "")
    }
}

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    validate: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = Int.MAX_VALUE,
    multiLine: Boolean = false,
    placeholder: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    rules: Rules = Rules.NoRules
) {
    val borderColor = Colors.inputBorderColor()

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier.clickable { focusRequester.requestFocus() },
        horizontalAlignment = horizontalAlignment,
    ) {
        label?.let { InputLabel(label) }

        val filters = mutableListOf<Filter>(Filter.MaxLength(maxLength))

        if (!multiLine) {
            filters.add(Filter.SingleLine)
        }

        val errorMessage = if (validate) rules.errorMessage(value) else null

        /**
         * We cannot simply set background to OutlinedTextField, because it has 8 dp top padding
         * space for label. So we have to get our hands dirty by calculating height of OutlinedTextField
         * and positioning our background surface behind it.
         */
        /**
         * We cannot simply set background to OutlinedTextField, because it has 8 dp top padding
         * space for label. So we have to get our hands dirty by calculating height of OutlinedTextField
         * and positioning our background surface behind it.
         */
        val density = AmbientDensity.current

        Layout(
            content = {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()
                ) { }
                OutlinedTextField(
                    value = value,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    onValueChange = {
                        val filteredValue =
                            filters.fold(it) { value, filter -> filter.process(value) }

                        if (filteredValue != value) {
                            onValueChange(filteredValue)
                        }
                    },
                    textStyle = AmbientTextStyle.current.copy(color = MaterialTheme.colors.onSurface),
                    modifier = Modifier.focusRequester(focusRequester).fillMaxWidth(),
                    inactiveColor = borderColor,
                    isErrorValue = errorMessage != null,
                    placeholder = placeholder?.let { { Text(it) } },
                )
            }
        ) { measurables, constraints ->
            val background = measurables[0]
            val textField = measurables[1]

            val textFieldTopPadding = with(density) { 8.dp.toIntPx() }
            val textFieldPlaceable = textField.measure(constraints)

            val height = max(0, textFieldPlaceable.height - textFieldTopPadding)

            layout(constraints.maxWidth, height) {
                background.measure(constraints.copy(minHeight = height)).place(0, 0)
                textFieldPlaceable.place(0, -textFieldTopPadding)
            }
        }

        if (errorMessage != null) {
            Text(
                errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
