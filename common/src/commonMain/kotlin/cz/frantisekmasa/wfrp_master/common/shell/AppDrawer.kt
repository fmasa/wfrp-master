package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.about.AboutScreen
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VISUAL_ONLY_ICON_DESCRIPTION
import cz.frantisekmasa.wfrp_master.common.localization.FixedStrings
import cz.frantisekmasa.wfrp_master.common.settings.SettingsScreen
import dev.icerock.moko.resources.compose.stringResource
import io.github.mmolosay.debounce.debounced
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AppDrawer(drawerState: DrawerState) {
    DrawerHeader()

    Column(Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        DrawerItem(
            icon = Icons.Rounded.Settings,
            text = stringResource(Str.settings_title),
            onClick = {
                coroutineScope.launch { drawerState.close() }
                if (navigator.lastItem !is SettingsScreen) {
                    // TODO: Make single-top
                    navigator.push(SettingsScreen)
                }
            },
        )

        val urlOpener = rememberUrlOpener()

        DrawerItem(
            icon = Icons.Rounded.MenuBook,
            text = stringResource(Str.drawer_wiki),
            onClick = {
                urlOpener.open(
                    FixedStrings.GITHUB_WIKI_URL,
                    isGooglePlayLink = false,
                )
            },
        )

        DrawerItem(
            icon = Icons.Rounded.Star,
            text = stringResource(Str.drawer_rate_app),
            onClick = {
                urlOpener.open(FixedStrings.GOOGLE_PLAY_URL, isGooglePlayLink = true)
            },
        )

        DrawerItem(
            icon = Icons.Rounded.Policy,
            text = stringResource(Str.drawer_privacy_policy),
            onClick =
                debounced(300.milliseconds) {
                    urlOpener.open(FixedStrings.PRIVACY_POLICY_URL, isGooglePlayLink = false)
                },
        )

        DrawerItem(
            icon = Icons.Rounded.BugReport,
            text = stringResource(Str.drawer_report_issue),
            onClick = {
                urlOpener.open(FixedStrings.GITHUB_ISSUES_URL, isGooglePlayLink = false)
            },
        )

        DrawerItem(
            icon = Icons.Rounded.Info,
            text = stringResource(Str.about_title),
            onClick = {
                coroutineScope.launch { drawerState.close() }

                if (navigator.lastItem !is AboutScreen) {
                    // TODO: Make single-top
                    navigator.push(AboutScreen)
                }
            },
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = debounced(300.milliseconds, onClick))
            .padding(vertical = Spacing.medium, horizontal = Spacing.large)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, VISUAL_ONLY_ICON_DESCRIPTION)
        Text(
            text,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        Modifier
            .padding(bottom = Spacing.small)
            .fillMaxWidth()
            .splashBackground(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(Spacing.large)
                .padding(top = Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                drawableResource(Resources.Drawable.SplashScreenIcon),
                VISUAL_ONLY_ICON_DESCRIPTION,
                Modifier.size(80.dp),
            )
            Text(
                FixedStrings.APP_NAME,
                style = MaterialTheme.typography.h6,
                color = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }
    }
}
