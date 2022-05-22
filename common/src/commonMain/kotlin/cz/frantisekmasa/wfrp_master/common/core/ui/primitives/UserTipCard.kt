package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Highlight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.tips.DismissedUserTipsHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.coroutines.launch
import org.kodein.di.compose.rememberInstance

@Composable
fun UserTipCard(tip: UserTip, modifier: Modifier = Modifier) {
    val dismissedTipsHolder: DismissedUserTipsHolder by rememberInstance()
    val dismissedTips = dismissedTipsHolder.dismissedTips.collectWithLifecycle(null).value

    if (dismissedTips == null || tip in dismissedTips) {
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        var processing by remember { mutableStateOf(false) }

        val strings = LocalStrings.current.commonUi

        AlertDialog(
            onDismissRequest = { dialogVisible = false },
            text = {
                Text(
                    strings.dismissTipConfirmation,
                    style = MaterialTheme.typography.body1,
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !processing,
                    onClick = {
                        processing = true

                        coroutineScope.launch { dismissedTipsHolder.dismissTip(tip) }
                    }
                ) {
                    Text(remember { strings.buttonDismiss.uppercase() })
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !processing,
                    onClick = { dialogVisible = false },
                ) {
                    Text(remember { strings.buttonKeep.uppercase() })
                }
            }
        )
    }

    val isLightTheme = MaterialTheme.colors.isLight

    Card(
        backgroundColor = if (isLightTheme)
            Theme.fixedColors.warning
        else MaterialTheme.colors.surface,
        border = if (isLightTheme) null else BorderStroke(1.dp, Theme.fixedColors.warning),
        modifier = modifier.clickable { dialogVisible = true },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Spacing.small),
        ) {
            Icon(Icons.Rounded.Highlight, null, Modifier.padding(end = Spacing.medium))
            Text(
                tip.localizedName,
                style = MaterialTheme.typography.body2,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

enum class UserTip(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
    ARMOUR_TRAPPINGS({ it.armour.tipTrappings }),
    DEPRECATED_LEGACY_ARMOUR({ it.armour.tipDeprecatedLegacyArmour }),
}