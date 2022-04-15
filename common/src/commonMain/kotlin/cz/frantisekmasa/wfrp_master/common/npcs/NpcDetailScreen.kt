package cz.frantisekmasa.wfrp_master.common.npcs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.encounters.EncounterDetailScreenModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.npcs.form.FormData
import cz.frantisekmasa.wfrp_master.common.npcs.form.NpcForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NpcDetailScreen(
    private val npcId: NpcId,
) : Screen {

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val viewModel: EncounterDetailScreenModel = rememberScreenModel(arg = npcId.encounterId)

        val npc = remember { viewModel.npcFlow(npcId) }.collectWithLifecycle().value
        val data = npc?.let { FormData.fromExistingNpc(it) }

        val validate = remember { mutableStateOf(false) }
        val submitEnabled = remember { mutableStateOf(true) }

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopBar(
                    title = LocalStrings.current.npcs.title,
                    onSave = {
                        if (data == null) {
                            return@TopBar
                        }

                        if (!data.isValid()) {
                            validate.value = true
                        } else {
                            submitEnabled.value = false
                        }

                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.updateNpc(
                                id = npc.id,
                                name = data.name.value,
                                note = data.note.value,
                                maxWounds = data.wounds.value.toInt(),
                                stats = data.characteristics.toCharacteristics(),
                                armor = data.armor.toArmor(),
                                enemy = data.enemy.value,
                                alive = data.alive.value,
                                traits = emptyList(),
                                trappings = emptyList(),
                            )

                            navigator.pop()
                        }
                    },
                    actionsEnabled = submitEnabled.value && data != null,
                )
            }
        ) {
            if (data == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                NpcForm(data, validate = validate.value)
            }
        }
    }

    @Composable
    private fun TopBar(
        title: String,
        onSave: () -> Unit,
        actionsEnabled: Boolean,
    ) {
        TopAppBar(
            navigationIcon = { BackButton() },
            title = { Text(title) },
            actions = { SaveAction(onClick = onSave, enabled = actionsEnabled) }
        )
    }
}
