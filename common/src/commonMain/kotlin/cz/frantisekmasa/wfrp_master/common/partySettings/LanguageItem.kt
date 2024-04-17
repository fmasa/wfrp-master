package cz.frantisekmasa.wfrp_master.common.partySettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun LanguageItem(language: Language, screenModel: PartySettingsScreenModel) {
    var dialogVisible by rememberSaveable { mutableStateOf(false) }

    ListItem(
        text = { Text(stringResource(Str.parties_label_language)) },
        secondaryText = { Text(language.localizedName) },
        modifier = Modifier.clickable { dialogVisible = true },
    )

    if (dialogVisible) {
        PartyLanguageDialog(
            currentLanguage = language,
            screenModel = screenModel,
            onDismissRequest = { dialogVisible = false },
        )
    }
}

@Composable
private fun PartyLanguageDialog(
    currentLanguage: Language,
    screenModel: PartySettingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var newLanguage by rememberSaveable { mutableStateOf(currentLanguage) }
        var saving by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.parties_label_language)) },
                    navigationIcon = { CloseButton(onDismissRequest) },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        val snackbarHolder = LocalPersistentSnackbarHolder.current
                        val unknownError = stringResource(Str.messages_error_unknown)

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                saving = true
                                coroutineScope.launchLogged(Dispatchers.IO) {
                                    try {
                                        screenModel.changeLanguage(newLanguage)
                                        onDismissRequest()
                                    } catch (e: Throwable) {
                                        snackbarHolder.showSnackbar(unknownError)
                                        saving = false
                                    }
                                }
                            }
                        )
                    }
                )
            },
        ) {
            if (saving) {
                FullScreenProgress()
                return@Scaffold
            }

            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding)
            ) {
                SelectBox(
                    value = newLanguage,
                    items = remember {
                        Language.values()
                            .map { it to it.localizedName }
                            .sortedBy { it.second }
                    },
                    onValueChange = { newLanguage = it },
                )

                HorizontalLine(Modifier.padding(vertical = Spacing.medium))

                val isLightTheme = MaterialTheme.colors.isLight
                Card(
                    backgroundColor = if (isLightTheme)
                        Theme.fixedColors.warning
                    else MaterialTheme.colors.surface,
                    border = if (isLightTheme) null else BorderStroke(1.dp, Theme.fixedColors.warning),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(Spacing.small),
                    ) {
                        Icon(Icons.Rounded.Warning, null, Modifier.padding(end = Spacing.medium))
                        Text(
                            stringResource(Str.parties_language_change_warning),
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                RichText {
                    Markdown(stringResource(Str.parties_language_explanation))
                }
            }
        }
    }
}
