package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun DiagnosedSwitch(
    diagnosed: Boolean,
    onChange: suspend (Boolean) -> Unit,
) {
    if (diagnosed) {
        DiagnosedSwitch(
            icon = Icons.Rounded.Visibility,
            text = stringResource(Str.diseases_label_diagnosed),
            secondaryText = stringResource(Str.diseases_messages_visible_to_player_true),
            checked = true,
            onChange = onChange,
        )
    } else {
        DiagnosedSwitch(
            icon = Icons.Rounded.VisibilityOff,
            text = stringResource(Str.diseases_label_not_diagnosed),
            secondaryText = stringResource(Str.diseases_messages_visible_to_player_false),
            checked = false,
            onChange = onChange,
        )
    }
}

@Composable
private fun DiagnosedSwitch(
    icon: ImageVector,
    text: String,
    secondaryText: String,
    checked: Boolean,
    onChange: suspend (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.padding(end = Spacing.small),
        )
        ListItem(
            text = { Text(text) },
            secondaryText = { Text(secondaryText) },
            trailing = {
                val coroutineScope = rememberCoroutineScope()

                val (saving, setSaving) = remember { mutableStateOf(false) }

                Switch(
                    checked = checked,
                    enabled = !saving,
                    onCheckedChange = {
                        if (saving) {
                            return@Switch
                        }

                        coroutineScope.launchLogged(Dispatchers.IO) {
                            setSaving(true)

                            try {
                                onChange(!checked)
                            } finally {
                                setSaving(false)
                            }
                        }
                    },
                )
            },
        )
    }
}
