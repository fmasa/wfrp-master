package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
internal fun TimesTakenForm(
    existingTalent: Talent?,
    compendiumTalentId: UUID,
    viewModel: TalentsViewModel,
    onDismissRequest: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var timesTaken by rememberSaveable { mutableStateOf(existingTalent?.taken ?: 1) }

    val strings = LocalStrings.current.talents
    val snackbarHolder = LocalPersistentSnackbarHolder.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = {
                    Text(if (existingTalent != null) strings.titleEdit else strings.titleEdit)
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    LocalContext.current

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                try {
                                    viewModel.saveCompendiumTalent(
                                        talentId = existingTalent?.id ?: UUID.randomUUID(),
                                        compendiumTalentId = compendiumTalentId,
                                        timesTaken = timesTaken,
                                    )
                                } catch (e: CompendiumItemNotFound) {
                                    Napier.d(e.toString(), e)

                                    snackbarHolder.showSnackbar(
                                        strings.messages.compendiumTalentRemoved
                                    )
                                } finally {
                                    withContext(Dispatchers.Main) { onDismissRequest() }
                                }
                            }
                        }
                    )
                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.labelTimesTaken,
                    modifier = Modifier.weight(1f),
                )
                NumberPicker(
                    value = timesTaken,
                    onIncrement = { timesTaken++ },
                    onDecrement = {
                        if (timesTaken > 1) {
                            timesTaken--
                        }
                    }
                )
            }
        }
    }
}
