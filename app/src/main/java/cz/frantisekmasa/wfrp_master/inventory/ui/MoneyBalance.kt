package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.muni.fi.rpg.R

@Composable
fun MoneyBalance(value: Money, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        ProvideTextStyle(MaterialTheme.typography.body1) {
            MoneyIcon(colorGold)
            Text(value.getCrowns().toString() + " " + stringResource(R.string.gold_coins_shortcut))

            MoneyIcon(colorSilver)
            Text(value.getShillings().toString() + " " + stringResource(R.string.silver_shillings_shortcut))

            MoneyIcon(colorBrass)
            Text(value.getPennies().toString() + " " + stringResource(R.string.brass_pennies_shortcut))
        }
    }
}

@Composable
private fun MoneyIcon(tint: Color) {
    Icon(
        painterResource(R.drawable.ic_coins),
        VisualOnlyIconDescription,
        tint = tint,
        modifier = Modifier.size(18.dp)
    )
}

val colorGold = Color(255, 183, 77)
val colorSilver = Color(158, 158, 158)
val colorBrass = Color(141, 110, 99)
