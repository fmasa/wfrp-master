package cz.muni.fi.rpg.ui.shell

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.navigate
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(drawerState: DrawerState, navController: NavHostController) {
    DrawerHeader()

    Column(Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()
        val strings = LocalStrings.current

        DrawerItem(
            icon = Icons.Rounded.Settings,
            text = strings.settings.title,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate(Route.Settings) { launchSingleTop = true }
            },
        )

        val context = LocalContext.current
        DrawerItem(
            icon = Icons.Rounded.Star,
            text = strings.drawer.rateApp,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(strings.contact.googlePlayUrl)
                    setPackage("com.android.vending")
                }

                context.startActivity(intent)
            },
        )

        DrawerItem(
            icon = Icons.Rounded.Policy,
            text = strings.drawer.privacyPolicy,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strings.contact.privacyPolicyUrl))
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                context.startActivity(intent)
            }
        )

        DrawerItem(
            icon = Icons.Rounded.BugReport,
            text = strings.drawer.reportIssue,
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "plain/text"
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(strings.contact.emailAddress)
                    )
                    putExtra(Intent.EXTRA_SUBJECT, strings.contact.bugReportEmailSubject)
                }

                context.startActivity(Intent.createChooser(intent, ""))
            },
        )

        DrawerItem(
            icon = Icons.Rounded.Info,
            text = strings.about.title,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate(Route.About) { launchSingleTop = true }
            },
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.medium, horizontal = Spacing.large)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, VisualOnlyIconDescription)
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
                VisualOnlyIconDescription,
                Modifier.size(80.dp),
            )
            Text(
                LocalStrings.current.about.appName,
                style = MaterialTheme.typography.h6,
                color = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }
    }
}
