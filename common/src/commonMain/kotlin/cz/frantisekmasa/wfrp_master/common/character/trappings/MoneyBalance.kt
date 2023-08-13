package cz.frantisekmasa.wfrp_master.common.character.trappings

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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MoneyBalance(value: Money, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        ProvideTextStyle(MaterialTheme.typography.body1) {
            MoneyIcon(colorGold)
            Text(
                buildString {
                    append(value.getCrowns())
                    append(" ")
                    append(stringResource(Str.trappings_money_gold_coins_shortcut))
                }
            )

            MoneyIcon(colorSilver)
            Text(
                buildString {
                    append(value.getShillings())
                    append(" ")
                    append(stringResource(Str.trappings_money_silver_shillings_shortcut))
                }
            )

            MoneyIcon(colorBrass)
            Text(
                buildString {
                    append(value.getPennies())
                    append(" ")
                    append(stringResource(Str.trappings_money_brass_pennies_shortcut))
                }
            )
        }
    }
}

@Composable
private fun MoneyIcon(tint: Color) {
    Icon(
        drawableResource(Resources.Drawable.TrappingCoins),
        VisualOnlyIconDescription,
        tint = tint,
        modifier = Modifier.size(18.dp)
    )
}

val colorGold = Color(255, 183, 77)
val colorSilver = Color(158, 158, 158)
val colorBrass = Color(141, 110, 99)
