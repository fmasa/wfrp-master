package cz.frantisekmasa.wfrp_master.common.compendium.journal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.VisibilitySwitchBar
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

class JournalEntryScreen(
    private val partyId: PartyId,
    private val entryId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: JournalScreenModel = rememberScreenModel(arg = partyId)

        val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(null).value
        val entryOrError = remember { screenModel.get(entryId) }
            .collectWithLifecycle(null).value

        if (entryOrError == null || isGameMaster == null) {
            FullScreenProgress()
            return
        }

        val navigation = LocalNavigationTransaction.current

        val entry = entryOrError.orNull()

        if (entry == null || (!entry.isVisibleToPlayers && !isGameMaster)) {
            val message = stringResource(Str.common_ui_item_does_not_exist)
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(message)
                navigation.goBack()
            }
            return
        }

        var editDialogOpened by remember { mutableStateOf(false) }

        if (editDialogOpened) {
            JournalEntryDialog(
                entry = entry,
                onDismissRequest = { editDialogOpened = false },
                onSaveRequest = screenModel::save,
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(entry.name) },
                    actions = {
                        if (isGameMaster) {
                            IconAction(
                                Icons.Rounded.Edit,
                                stringResource(Str.common_ui_button_edit),
                                onClick = { editDialogOpened = true },
                            )
                        }
                    },
                )
            }
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (isGameMaster) {
                    VisibilitySwitchBar(
                        visible = entry.isVisibleToPlayers,
                        onChange = { screenModel.save(entry.changeVisibility(it)) },
                    )
                }

                Column(
                    Modifier
                        .padding(Spacing.bodyPadding)
                        .fillMaxWidth()
                ) {
                    if (isGameMaster) {
                        val coroutineScope = rememberCoroutineScope()
                        var saving by remember { mutableStateOf(false) }
                        val pinToggle: () -> Unit = {
                            saving = true
                            coroutineScope.launchLogged(Dispatchers.IO) {
                                try {
                                    screenModel.save(entry.copy(isPinned = !entry.isPinned))
                                } finally {
                                    saving = false
                                }
                            }
                        }

                        Box(
                            Modifier.align(Alignment.CenterHorizontally)
                                .padding(bottom = Spacing.medium)
                        ) {
                            if (entry.isPinned) {
                                Button(
                                    onClick = pinToggle,
                                    enabled = !saving,
                                ) {
                                    PinIcon()
                                    Text(stringResource(Str.journal_folder_unpin_cta))
                                }
                            } else {
                                OutlinedButton(
                                    onClick = pinToggle,
                                    enabled = !saving,
                                ) {
                                    PinIcon()
                                    Text(stringResource(Str.journal_folder_pin_cta))
                                }
                            }
                        }
                    }

                    RichText {
                        SingleLineTextValue(
                            stringResource(Str.journal_label_parents),
                            remember(entry.parents) {
                                entry.parents
                                    .joinToString(" ${JournalEntry.PARENT_SEPARATOR}")
                            }
                        )
                        Markdown(entry.text)
                    }

                    if (isGameMaster && entry.gmText.isNotEmpty()) {
                        CardContainer(Modifier.padding(top = Spacing.medium)) {
                            CardTitle(stringResource(Str.journal_title_gm_text))

                            RichText {
                                Markdown(entry.gmText)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PinIcon() {
        Box(Modifier.padding(end = Spacing.small)) {
            Icon(
                Icons.Rounded.PushPin,
                VisualOnlyIconDescription,
                Modifier.size(16.dp)
            )
        }
    }
}
