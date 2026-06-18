package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Countdown
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun CountdownInput(
    label: String,
    helperText: String? = null,
    data: CountdownInputData,
    validate: Boolean,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = Spacing.tiny),
        )

        helperText?.let {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
                RichText(Modifier.padding(bottom = Spacing.tiny)) {
                    Markdown(it)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TextInput(
                modifier = Modifier.weight(2f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = data.value,
                validate = validate,
            )

            SelectBox(
                modifier = Modifier.weight(1f),
                items = Countdown.Unit.entries,
                value = data.unit.value,
                onValueChange = { data.unit.value = it },
            )
        }
    }
}

data class CountdownInputData(
    val value: InputValue,
    val unit: MutableState<Countdown.Unit>,
) {
    fun toValue(): Countdown {
        return Countdown(
            value = value.toInt(),
            unit = unit.value,
        )
    }

    fun isValid(): Boolean = value.isValid()
}
