package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

interface Filter {
    companion object {
        val DigitsAndDotSymbolsOnly: Filter = AllowedCharacters(('0'..'9').toList() + '.')
    }

    fun process(value: String): String

    private class AllowedCharacters(private val characters: List<Char>) : Filter {
        override fun process(value: String) = value.filter { it in characters }
    }

    class MaxLength(private val maxLength: Int) : Filter {
        override fun process(value: String) = if (value.length <= maxLength)
            value
        else value.substring(0, maxLength)
    }

    object SingleLine : Filter {
        override fun process(value: String): String = value.replace("\n", "")
    }
}

// TODO: Use this version everywhere where input must be validated
// as it allows to define validation only on InputValue and then use value.isValid()
@Composable
fun TextInput(
    value: InputValue,
    validate: Boolean,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    label: String? = null,
    helperText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLength: Int = Int.MAX_VALUE,
    showCharacterCount: Boolean = maxLength < Int.MAX_VALUE,
    multiLine: Boolean = false,
    placeholder: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    filters: List<Filter> = emptyList(),
) {
    TextInput(
        value = value.value,
        onValueChange = { value.value = it },
        label = label,
        helperText = helperText,
        validate = validate,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        maxLength = maxLength,
        multiLine = multiLine,
        placeholder = placeholder,
        horizontalAlignment = horizontalAlignment,
        filters = filters,
        modifier = modifier,
        textFieldModifier = textFieldModifier,
        showCharacterCount = showCharacterCount,
        rules = value.rules,
        visualTransformation = visualTransformation,
    )
}

@Composable
private fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    validate: Boolean,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier,
    label: String? = null,
    helperText: String? = null,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    maxLength: Int = Int.MAX_VALUE,
    showCharacterCount: Boolean,
    multiLine: Boolean = false,
    placeholder: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    rules: Rules = Rules.NoRules,
    filters: List<Filter> = emptyList(),
) {
    Column(horizontalAlignment = horizontalAlignment, modifier = modifier) {
        label?.let { InputLabel(label) }

        val allFilters = filters.toMutableList()
        allFilters.add(Filter.MaxLength(maxLength))

        if (!multiLine) {
            allFilters.add(Filter.SingleLine)
        }

        val errorMessage = if (validate) rules.errorMessage(value) else null
        val borderColor = if (errorMessage != null)
            MaterialTheme.colors.error
        else Colors.inputBorderColor()

        Surface(
            shape = RoundedCornerShape(Spacing.tiny),
            border = BorderStroke(1.dp, borderColor),
            color = if (MaterialTheme.colors.isLight)
                MaterialTheme.colors.surface else
                Color(33, 33, 33), // TODO: Move to Theme
        ) {
            val textStyle = LocalTextStyle.current
            val textColor = MaterialTheme.colors.onSurface

            Box(contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = value,
                    onValueChange = {
                        val filteredValue =
                            allFilters.fold(it) { value, filter -> filter.process(value) }

                        if (filteredValue != value) {
                            onValueChange(filteredValue)
                        }
                    },
                    cursorBrush = SolidColor(textColor),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    textStyle = textStyle.copy(color = textColor),
                    singleLine = !multiLine,
                    visualTransformation = visualTransformation,
                    modifier = textFieldModifier
                        .fillMaxWidth()
                        .padding(Spacing.medium)
                        .then(if (multiLine) Modifier.heightIn(min = 48.dp) else Modifier),
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

        if (showCharacterCount || !helperText.isNullOrBlank()) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row {
                    if (!helperText.isNullOrBlank()) {
                        Text(helperText, style = MaterialTheme.typography.body2)
                    }

                    Spacer(Modifier.weight(1f))

                    if (showCharacterCount) {
                        Text(
                            "${value.length} / $maxLength",
                            style = MaterialTheme.typography.overline,
                        )
                    }
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            ErrorMessage(errorMessage)
        }
    }
}

@Stable
class InputValue(
    state: MutableState<String>,
    internal val rules: Rules,
    private val normalize: (String) -> String = { it.trim() },
) {
    var value: String by state
    val normalizedValue get() = normalize(value)

    fun isValid() = rules.errorMessage(value) == null

    fun toInt(): Int = value.toInt()
    fun toIntOrNull(): Int? = value.toIntOrNull()
    fun toDouble(): Double = value.toDouble()
}

@Composable
fun inputValue(default: String) = InputValue(rememberSaveable { mutableStateOf(default) }, Rules.NoRules)

@Composable
fun inputValue(default: String, vararg rules: Rule) = InputValue(
    rememberSaveable { mutableStateOf(default) },
    Rules(*rules),
)

@Composable
@Stable
inline fun <reified T> expressionInputValue(
    default: String,
    vararg rules: Rule,
): InputValue where T : Enum<T>, T : Expression.Constant {
    val format = Expression.formatter<T>()
    val normalize = Expression.normalizer<T>()

    return InputValue(
        rememberSaveable { mutableStateOf(format(default)) },
        Rules(
            *rules
                .map { RuleWrapper(it, normalize) }
                .toTypedArray(),
        ),
        normalize = normalize,
    )
}
