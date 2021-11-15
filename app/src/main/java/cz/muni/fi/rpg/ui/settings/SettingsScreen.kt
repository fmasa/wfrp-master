package cz.muni.fi.rpg.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.components.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.components.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.SettingsViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.provideSettingsViewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(routing: Routing<Route.Settings>) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = { routing.pop() })
                },
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colors.background)
                .padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            val viewModel = provideSettingsViewModel()

            SignInCard(viewModel, routing)

            SettingsCard {
                val premiumViewModel = providePremiumViewModel()
                val premiumActive = premiumViewModel.active == true

                SettingsTitle(R.string.settings_general)
                SoundCard(viewModel)
                DarkModeCard(viewModel)

                if (!premiumActive) {
                    PersonalizedAds(viewModel)

                    SettingsTitle(R.string.settings_premium)
                    BuyPremiumButton(premiumViewModel)
                }
            }
        }
    }
}

@Composable
private fun BuyPremiumButton(viewModel: PremiumViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current

    ListItem(
        modifier = Modifier.clickable {
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.purchasePremium(activity)
            }
        },
        icon = { Icon(painterResource(R.drawable.ic_premium), VisualOnlyIconDescription) },
        text = { Text(stringResource(R.string.buy_premium)) },
    )
}

@Composable
private fun DarkModeCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_dark_mode,
        value = viewModel.darkMode.collectWithLifecycle(isSystemInDarkTheme()).value,
        onChange = { viewModel.toggleDarkMode(it) },
    )
}

@Composable
private fun SoundCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_sound,
        value = viewModel.soundEnabled.collectWithLifecycle(null).value,
        onChange = { viewModel.toggleSound(it) }
    )
}

@Composable
private fun PersonalizedAds(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_personalized_ads,
        value = viewModel.personalizedAds.collectWithLifecycle(null).value,
        onChange = { viewModel.togglePersonalizedAds(it) },
    )
}

@Composable
private fun SwitchItem(
    @StringRes name: Int,
    value: Boolean?,
    onChange: suspend (newValue: Boolean) -> Unit,
    disabledText: String? = null,
    enabled: Boolean = true,
) {
    val color = LocalContentColor.current.copy(
        alpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled
    )

    ListItem(
        text = {
            Text(stringResource(name), color = color)
        },
        secondaryText = disabledText?.let { { Text(disabledText, color = color) } },
        trailing = {
            val checked = value ?: return@ListItem
            val coroutineScope = rememberCoroutineScope()

            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = {
                    coroutineScope.launch {
                        onChange(!checked)
                    }
                }
            )
        }
    )
}
