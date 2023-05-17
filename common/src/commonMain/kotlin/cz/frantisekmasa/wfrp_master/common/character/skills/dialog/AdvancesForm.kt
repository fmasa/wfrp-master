package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
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
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillRating
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun AdvancesForm(
    compendiumSkillId: Uuid,
    characteristics: Stats,
    screenModel: SkillsScreenModel,
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
                    val snackbarHolder = LocalPersistentSnackbarHolder.current
                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                try {
                                    screenModel.saveCompendiumSkill(
                                        skillId = uuid4(),
                                        compendiumSkillId = compendiumSkillId,
                                        advances = advances,
                                    )
                                } catch (e: CompendiumItemNotFound) {
                                    Napier.d(e.toString(), e)

                                    snackbarHolder.showSnackbar(
                                        strings.messages.compendiumSkillRemoved,
                                        SnackbarDuration.Short,
                                    )
                                } finally {
                                    onDismissRequest()
                                }
                            }
                        }
                    )
                }
            )
        }
    ) {
        val skill = screenModel.compendiumItems
            .collectWithLifecycle(null)
            .value
            ?.firstOrNull { it.id == compendiumSkillId }

        if (saving || skill == null) {
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

            SkillRating(
                label = skill.name,
                value = characteristics.get(skill.characteristic) + advances,
                modifier = Modifier
                    .padding(top = Spacing.large)
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}
