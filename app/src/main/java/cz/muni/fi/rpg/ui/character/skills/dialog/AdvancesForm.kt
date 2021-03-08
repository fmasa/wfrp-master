package cz.muni.fi.rpg.ui.character.skills.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@Composable
internal fun AdvancesForm(
    compendiumSkillId: UUID,
    viewModel: SkillsViewModel,
    onDismissRequest: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var advances by rememberSaveable { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = {
                    Text(
                        stringResource(R.string.title_skill_new)
                    )
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    val context = LocalContext.current

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                try {
                                    viewModel.saveCompendiumSkill(
                                        skillId = UUID.randomUUID(),
                                        compendiumSkillId = compendiumSkillId,
                                        advances = advances,
                                    )
                                } catch (e: CompendiumItemNotFound) {
                                    Timber.d(e)

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_compendium_skill_removed),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
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
                    text = stringResource(R.string.label_advances),
                    modifier = Modifier.weight(1f),
                )
                NumberPicker(
                    value = advances,
                    onIncrement = { advances++ },
                    onDecrement = {
                        if (advances > 1) {
                            advances--
                        }
                    }
                )
            }
        }
    }
}