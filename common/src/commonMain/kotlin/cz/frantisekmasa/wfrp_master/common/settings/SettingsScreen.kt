package cz.frantisekmasa.wfrp_master.common.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.settings.SettingsTitle
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: SettingsScreenModel = rememberScreenModel()
        val language = screenModel.language.collectWithLifecycle(null).value

        key(language) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = { BackButton() },
                        title = { Text(stringResource(Str.settings_title)) },
                    )
                },
            ) {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colors.background)
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    SignInCard(screenModel)

                    SettingsCard {
                        SettingsTitle(stringResource(Str.settings_title_general))
                        SoundCard(screenModel)
                        DarkModeCard(screenModel)
                        LanguageCard(screenModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkModeCard(viewModel: SettingsScreenModel) {
    SwitchItem(
        name = stringResource(Str.settings_dark_mode),
        value = viewModel.darkMode.collectWithLifecycle(null).value ?: isSystemInDarkTheme(),
        onChange = { viewModel.toggleDarkMode(it) },
    )
}

@Composable
private fun SoundCard(viewModel: SettingsScreenModel) {
    SwitchItem(
        name = stringResource(Str.settings_sound),
        value = viewModel.soundEnabled.collectWithLifecycle(null).value ?: false,
        onChange = { viewModel.toggleSound(it) },
    )
}

@Composable
private fun LanguageCard(screenModel: SettingsScreenModel) {
    val language = screenModel.language.collectWithLifecycle(null).value ?: Language.EN

    val (dialogOpened, setDialogOpened) = remember { mutableStateOf(false) }

    if (dialogOpened) {
        val coroutineScope = rememberCoroutineScope()

        LanguageDialog(
            selected = language,
            onSelect = {
                coroutineScope.launch(Dispatchers.IO) {
                    screenModel.updateLanguage(it)
                    setDialogOpened(false)
                }
            },
            onDismissRequest = { setDialogOpened(false) },
        )
    }

    ListItem(
        text = { Text(stringResource(Str.settings_language)) },
        secondaryText = { Text(language.localizedName) },
        modifier = Modifier.clickable { setDialogOpened(true) },
    )
}

@Composable
private fun SwitchItem(
    name: String,
    value: Boolean?,
    onChange: suspend (newValue: Boolean) -> Unit,
    enabled: Boolean = true,
) {
    val color =
        LocalContentColor.current.copy(
            alpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled,
        )

    ListItem(
        text = {
            Text(name, color = color)
        },
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
                },
            )
        },
    )
}
