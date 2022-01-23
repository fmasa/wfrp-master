package cz.muni.fi.rpg.ui.character.skills.dialog

import android.widget.Toast
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
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
internal fun AdvancesForm(
    compendiumSkillId: UUID,
    viewModel: SkillsViewModel,
    isAdvanced: Boolean,
    onDismissRequest: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var advances by rememberSaveable { mutableStateOf(1) }

    val strings = LocalStrings.current.skills

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(strings.titleNew) },
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
                                    Napier.d(e.toString(), e)

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            strings.messages.compendiumSkillRemoved,
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
                    text = strings.labelAdvances,
                    modifier = Modifier.weight(1f),
                )

                val minAdvances = if (isAdvanced) 1 else 0

                NumberPicker(
                    value = advances,
                    onIncrement = { advances++ },
                    onDecrement = {
                        if (advances > minAdvances) {
                            advances--
                        }
                    }
                )
            }
        }
    }
}
