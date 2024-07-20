package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellLoreIcon
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SpellDetail(
    spell: Spell,
    onDismissRequest: () -> Unit,
    subheadBar: @Composable ColumnScope.() -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(spell.name) },
            )
        },
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            subheadBar()

            SpellDetailBody(
                castingNumber = spell.castingNumber,
                effectiveCastingNumber = spell.effectiveCastingNumber,
                range = spell.range,
                target = spell.target,
                lore = spell.lore,
                duration = spell.duration,
                effect = spell.effect,
            )
        }
    }
}

@Composable
fun SpellDetailBody(
    castingNumber: Int,
    effectiveCastingNumber: Int,
    range: String,
    target: String,
    lore: SpellLore?,
    duration: String,
    effect: String,
) {
    SelectionContainer {
        Column(Modifier.padding(Spacing.bodyPadding).fillMaxWidth()) {
            if (lore != null) {
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = Spacing.large),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SpellLoreIcon(lore)
                    Text(lore.localizedName, style = MaterialTheme.typography.h6)
                }
            }

            SingleLineTextValue(
                stringResource(Str.spells_casting_number_shortcut),
                buildAnnotatedString {
                    if (castingNumber != effectiveCastingNumber) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                            append(castingNumber.toString())
                        }

                        append(" ➔ ")
                    }

                    append(effectiveCastingNumber.toString())
                },
            )

            SingleLineTextValue(stringResource(Str.spells_label_range), range)

            SingleLineTextValue(stringResource(Str.spells_label_target), target)

            SingleLineTextValue(stringResource(Str.spells_label_duration), duration)

            RichText(Modifier.padding(top = 8.dp)) {
                Markdown(effect)
            }
        }
    }
}
