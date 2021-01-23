package cz.muni.fi.rpg.ui.shell

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.navigate
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.premium.providePremiumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun AppDrawer(drawerState: DrawerState, navController: NavHostController) {
    DrawerHeader()

    Column(
        Modifier
            .fillMaxSize()
    ) {
        PremiumItem()

        DrawerItem(
            icon = R.drawable.ic_settings,
            text = R.string.settings,
            onClick = {
                drawerState.close()
                navController.navigate(Route.Settings) { launchSingleTop = true }
            },
        )

        val context = AmbientContext.current
        DrawerItem(
            icon = R.drawable.ic_review,
            text = R.string.rate_app,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(context.getString(R.string.store_listing_url))
                    setPackage("com.android.vending")
                }

                context.startActivity(intent)
            },
        )

        DrawerItem(
            icon = R.drawable.ic_policy,
            text = R.string.label_privacy_policy,
            onClick = {
                val urlString = context.getString(R.string.privacy_policy_url)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(intent)
            }
        )

        DrawerItem(
            icon = R.drawable.ic_bug_report,
            text = R.string.report_issue,
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "plain/text"
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(context.getString(R.string.issue_email_address))
                    )
                    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.issue_email_subject))
                }

                context.startActivity(Intent.createChooser(intent, ""))
            },
        )

        DrawerItem(
            icon = R.drawable.ic_info,
            text = R.string.about,
            onClick = {
                drawerState.close()
                navController.navigate(Route.About) { launchSingleTop = true }
            },
        )
    }
}

@Composable
private fun PremiumItem() {
    val premiumViewModel = providePremiumViewModel()
    val coroutineScope = rememberCoroutineScope()
    val activity = AmbientActivity.current

    if (premiumViewModel.active == true) {
        return
    }

    DrawerItem(
        icon = R.drawable.ic_premium,
        text = R.string.buy_premium,
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                val result = premiumViewModel.purchasePremium(activity)
                Timber.d(result.toString())
            }
        },
        modifier = Modifier.padding(bottom = Spacing.tiny),
    )

    Divider()
}

@Composable
private fun DrawerItem(
    @DrawableRes icon: Int,
    @StringRes text: Int,
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
        Icon(vectorResource(icon))
        Text(
            stringResource(text),
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
            Image(vectorResource(R.drawable.splash_screen_image), Modifier.size(80.dp))
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.h6,
                color = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }
    }
}

