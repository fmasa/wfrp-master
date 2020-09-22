package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import kotlin.math.max

interface Filter {
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
    fun errorMessage(value: String): String? =
        rules.firstOrNull { (validator, _) -> !validator(value) }?.second
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
    multiLine: Boolean = false,
    placeholder: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    rules: Rules = Rules.NoRules
) {
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.15f)

    Column(modifier, horizontalAlignment = horizontalAlignment) {
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

@ExperimentalLayout
@Composable
fun <T> ChipList(
    label: String? = null,
    items: Iterable<Pair<T, String>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        label?.let {
            InputLabel(it)
        }

        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            for ((itemValue, itemLabel) in items) {
                Chip(
                    text = itemLabel,
                    selected = itemValue == value,
                    onClick = {
                        if (itemValue != value) {
                            onValueChange(itemValue)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    val textStyle = MaterialTheme.typography.body2
    val padding = 8.dp
    val borderRadius = with(DensityAmbient.current) { textStyle.fontSize.toDp() / 2 + padding }
    val color = if (selected) MaterialTheme.colors.primary else contentColor()

    Surface(
        shape = RoundedCornerShape(size = borderRadius),
        color = if (selected) color.copy(alpha = 0.15f) else color.copy(alpha = 0.1f),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.body2.copy(color = color),
            modifier = Modifier.clickable(onClick = onClick).padding(padding),
        )
    }
}

@Composable
fun <T> RadioList(
    label: String,
    items: Iterable<Pair<T, String>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier) {
        InputLabel(label)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for ((itemValue, itemLabel) in items) {
                val onClick = {
                    if (value != itemValue) {
                        onValueChange(itemValue)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                        selected = value == itemValue,
                        onClick = onClick,
                    )

                    Text(
                        itemLabel,
                        modifier = Modifier.clickable(onClick = onClick, indication = null),
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}

@Composable
private fun InputLabel(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.body2,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}
