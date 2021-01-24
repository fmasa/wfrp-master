package cz.muni.fi.rpg.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.components.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.core.ui.components.settings.SettingsTitle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.frantisekmasa.wfrp_master.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.SettingsViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.provideSettingsViewModel
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
        ScrollableColumn(
            Modifier
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
                }

                SettingsTitle(R.string.settings_premium)

                if (!premiumActive) {
                    BuyPremiumButton(premiumViewModel)
                }
            }
        }
    }
}

@Composable
private fun BuyPremiumButton(viewModel: PremiumViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val activity = AmbientActivity.current

    ListItem(
        modifier = Modifier.clickable {
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.purchasePremium(activity)
            }
        },
        icon = { Icon(vectorResource(R.drawable.ic_premium)) },
        text = { Text(stringResource(R.string.buy_premium)) },
    )
}

@Composable
private fun DarkModeCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_dark_mode,
        value = viewModel.darkMode.value ?: isSystemInDarkTheme(),
        onChange = { viewModel.toggleDarkMode(it) },
    )
}

@Composable
private fun SoundCard(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_sound,
        value = viewModel.soundEnabled.observeAsState().value,
        onChange = { viewModel.toggleSound(it) }
    )
}

@Composable
private fun PersonalizedAds(viewModel: SettingsViewModel) {
    SwitchItem(
        name = R.string.settings_personalized_ads,
        value = viewModel.personalizedAds.observeAsState().value,
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
    val color = AmbientContentColor.current.copy(
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
