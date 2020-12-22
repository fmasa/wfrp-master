package cz.muni.fi.rpg.ui.character.skills.dialog

import android.widget.Toast
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@Composable
internal fun AdvancesForm(
    existingSkill: Skill?,
    compendiumSkillId: UUID,
    viewModel: SkillsViewModel,
    onComplete: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var advances by savedInstanceState { existingSkill?.advances ?: 1 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingSkill != null)
                                R.string.title_skill_edit else
                                R.string.title_skill_new
                        )
                    )
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    val context = ContextAmbient.current

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                try {
                                    viewModel.saveCompendiumSkill(
                                        skillId = existingSkill?.id ?: UUID.randomUUID(),
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
                                    withContext(Dispatchers.Main) { onComplete() }
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

        ScrollableColumn(contentPadding = PaddingValues(BodyPadding)) {
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