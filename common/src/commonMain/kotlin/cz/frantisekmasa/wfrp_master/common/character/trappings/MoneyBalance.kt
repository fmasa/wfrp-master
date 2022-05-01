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
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
fun MoneyBalance(value: Money, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        ProvideTextStyle(MaterialTheme.typography.body1) {
            val labels = LocalStrings.current.trappings.money

            MoneyIcon(colorGold)
            Text("${value.getCrowns()} ${labels.goldCoinsShortcut}")

            MoneyIcon(colorSilver)
            Text("${value.getShillings()} ${labels.silverShillingsShortcut}")

            MoneyIcon(colorBrass)
            Text("${value.getPennies()} ${labels.brassPenniesShortcut}")
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
