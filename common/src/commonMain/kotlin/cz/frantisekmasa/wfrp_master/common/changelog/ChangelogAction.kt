package cz.frantisekmasa.wfrp_master.common.changelog

import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.settings.SettingsScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChangelogAction(
    settingsScreenModel: SettingsScreenModel,
    onClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentVersion = LocalStaticConfiguration.current.version

    IconButton(
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                settingsScreenModel.updateLastSeenVersion(currentVersion)
            }
            onClick()
        }
    ) {
        BadgedBox(
            badge = {
                val version by settingsScreenModel.lastSeenVersion.collectWithLifecycle("loading")

                if (version != "loading" && version != currentVersion) {
                    Badge(backgroundColor = Theme.fixedColors.accent)
                }
            }
        ) {
            Icon(
                Icons.Rounded.Notifications,
                LocalStrings.current.changelog.title,
                tint = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }
    }
}
