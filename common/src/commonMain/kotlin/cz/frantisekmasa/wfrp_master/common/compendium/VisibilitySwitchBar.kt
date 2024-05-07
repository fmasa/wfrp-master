package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun VisibilitySwitchBar(
    visible: Boolean,
    onChange: suspend (Boolean) -> Unit,
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (visible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.padding(end = Spacing.small),
                )

                Text(
                    if (visible) {
                        stringResource(Str.compendium_visible_to_players_true)
                    } else {
                        stringResource(Str.compendium_visible_to_players_false)
                    },
                )
            }

            val coroutineScope = rememberCoroutineScope()

            val (saving, setSaving) = remember { mutableStateOf(false) }

            Switch(
                checked = visible,
                enabled = !saving,
                onCheckedChange = {
                    if (saving) {
                        return@Switch
                    }

                    coroutineScope.launchLogged(Dispatchers.IO) {
                        setSaving(true)

                        try {
                            onChange(!visible)
                        } finally {
                            setSaving(false)
                        }
                    }
                },
            )
        }
    }
}
