package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
fun SpellDetail(
    spell: Spell,
    onDismissRequest: () -> Unit,
    onMemorizedChange: (memorized: Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(spell.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            val strings = LocalStrings.current.spells

            SubheadBar {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(strings.labelMemorized)
                    Switch(
                        checked = spell.memorized,
                        onCheckedChange = { onMemorizedChange(it) },
                    )
                }
            }

            Column(Modifier.padding(Spacing.bodyPadding)) {
                SingleLineTextValue(
                    strings.castingNumberShortcut,
                    buildAnnotatedString {
                        if (spell.castingNumber != spell.effectiveCastingNumber) {
                            withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                                append(spell.castingNumber.toString())
                            }

                            append(" ➔ ")
                        }

                        append(spell.effectiveCastingNumber.toString())
                    }
                )


                SingleLineTextValue(strings.labelRange, spell.range)

                SingleLineTextValue(strings.labelTarget, spell.target)

                SingleLineTextValue(strings.labelDuration, spell.duration)

                Text(
                    text = spell.effect,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
