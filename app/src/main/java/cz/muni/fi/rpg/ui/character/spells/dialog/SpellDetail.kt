package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.ui.common.composables.Theme
import java.util.*

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
            SubheadBar {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.spell_memorized))
                    Switch(
                        checked = spell.memorized,
                        onCheckedChange = { onMemorizedChange(it) },
                    )
                }
            }

            Column(Modifier.padding(BodyPadding)) {
                SingleLineTextValue(
                    R.string.spell_casting_number_shortcut,
                    with(AnnotatedString.Builder()) {
                        if (spell.castingNumber != spell.effectiveCastingNumber) {
                            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                            append(spell.castingNumber.toString())
                            pop()

                            append(" ➔ ")
                        }

                        append(spell.effectiveCastingNumber.toString())

                        toAnnotatedString()
                    }
                )

                SingleLineTextValue(R.string.label_spell_range, spell.range)

                SingleLineTextValue(R.string.label_spell_target, spell.target)

                SingleLineTextValue(R.string.label_spell_duration, spell.duration)

                Text(
                    text = spell.effect,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun SpellDetailPreview() {
    Theme {
        SpellDetail(
            Spell(
                id = UUID.randomUUID(),
                name = "Magic dart",
                effect = "Magic dart does this!\n".repeat(10),
                range = "None",
                target = "AoE (See description)",
                duration = "Instant",
                castingNumber = 0,
            ),
            onDismissRequest = {},
            onMemorizedChange = {},
        )
    }
}