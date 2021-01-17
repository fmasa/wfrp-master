package cz.muni.fi.rpg.ui.character.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.Theme

@Composable
fun MoneyBalance(value: Money, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        ProvideTextStyle(MaterialTheme.typography.body1) {
            MoneyIcon(Theme.fixedColors.currencyGold)
            Text(value.getCrowns().toString() + " " + stringResource(R.string.gold_coins_shortcut))

            MoneyIcon(Theme.fixedColors.currencySilver)
            Text(value.getShillings().toString() + " " + stringResource(R.string.silver_shillings_shortcut))

            MoneyIcon(Theme.fixedColors.currencyBrass)
            Text(value.getPennies().toString() + " " + stringResource(R.string.brass_pennies_shortcut))
        }
    }
}

@Composable
private fun MoneyIcon(tint: Color) {
    Icon(
        vectorResource(R.drawable.ic_coins),
        tint = tint,
        modifier = Modifier.size(18.dp)
    )
}