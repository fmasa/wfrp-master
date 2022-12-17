package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression

@Composable
@Stable
fun damageValue(damage: DamageExpression, strengthBonus: Int): AnnotatedString {
    return buildAnnotatedString {
        append("+")
        append(damage.value)
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
            append(" (+")
            append(
                damage.calculate(
                    strengthBonus = strengthBonus,
                    successLevels = 0,
                ).value.toString()
            )
            append(")")
        }
    }
}
