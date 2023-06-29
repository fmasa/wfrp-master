package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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
        }
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
    val strings = LocalStrings.current.spells

    Column(Modifier.padding(Spacing.bodyPadding)) {
        SingleLineTextValue(
            strings.castingNumberShortcut,
            buildAnnotatedString {
                if (castingNumber != effectiveCastingNumber) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(castingNumber.toString())
                    }

                    append(" ➔ ")
                }

                append(effectiveCastingNumber.toString())
            }
        )

        SingleLineTextValue(strings.labelRange, range)

        SingleLineTextValue(strings.labelTarget, target)

        SingleLineTextValue(strings.labelDuration, duration)

        if (lore != null) {
            SingleLineTextValue(strings.labelLore, lore.localizedName)
        }

        RichText(Modifier.padding(top = 8.dp)) {
            Markdown(effect)
        }
    }
}
