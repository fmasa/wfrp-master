package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing

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
    val focusRequester = remember { FocusRequester() }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier.clickable(indication = null) { focusRequester.requestFocus() },
    ) {
        label?.let { InputLabel(label) }

        val filters = mutableListOf<Filter>(Filter.MaxLength(maxLength))

        if (!multiLine) {
            filters.add(Filter.SingleLine)
        }

        val errorMessage = if (validate) rules.errorMessage(value) else null
        val borderColor = if (errorMessage != null)
            MaterialTheme.colors.error
        else Colors.inputBorderColor()

        Surface(
            shape = RoundedCornerShape(Spacing.tiny),
            border = BorderStroke(1.dp, borderColor),
            color = MaterialTheme.colors.surface,
        ) {
            val textStyle = AmbientTextStyle.current
            val textColor = MaterialTheme.colors.onSurface

            Box(contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = value,
                    onValueChange = {
                        val filteredValue =
                            filters.fold(it) { value, filter -> filter.process(value) }

                        if (filteredValue != value) {
                            onValueChange(filteredValue)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    textStyle = textStyle.copy(color = textColor),
                    singleLine = !multiLine,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .padding(Spacing.medium)
                )

                if (placeholder != null && value.isEmpty()) {
                    Text(
                        placeholder,
                        Modifier.padding(Spacing.medium),
                        color = textColor.copy(alpha = ContentAlpha.medium),
                    )
                }
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
