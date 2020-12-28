package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.ui.common.composables.Theme
import java.util.*

@Composable
fun SpellDetail(
    spell: Spell,
    onDismissRequest: () -> Unit,
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
        Column(Modifier.padding(BodyPadding)) {
            TextItem(
                label = stringResource(R.string.spell_casting_number_shortcut),
                value = spell.castingNumber.toString(),
            )

            TextItem(
                label = stringResource(R.string.label_spell_range),
                value = spell.range,
            )

            TextItem(
                label = stringResource(R.string.label_spell_target),
                value = spell.target,
            )

            TextItem(
                label = stringResource(R.string.label_spell_duration),
                value = spell.duration,
            )

            Text(
                text = spell.effect,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun TextItem(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
        )
        Text(value)
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
        )
    }
}