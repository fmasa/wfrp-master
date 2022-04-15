package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.spells.SpellsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
internal fun CompendiumSpellChooser(
    screenModel: SpellsScreenModel,
    onComplete: () -> Unit,
    onCustomSpellRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val strings = LocalStrings.current.spells

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(strings.titleChooseCompendiumSpell) },
            )
        }
    ) {
        val compendiumSpells = screenModel.notUsedSpellsFromCompendium.collectWithLifecycle(null).value
        val totalCompendiumSpellCount = screenModel.compendiumSpellsCount.collectWithLifecycle(null).value
        var saving by remember { mutableStateOf(false) }

        if (compendiumSpells == null || totalCompendiumSpellCount == null || saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumSpells.isEmpty()) {

                    EmptyUI(
                        icon = Resources.Drawable.Spell,
                        text = strings.messages.noSpellsInCompendium,
                        subText = when (totalCompendiumSpellCount) {
                            0 -> strings.messages.noSpellsInCompendium
                            else -> null
                        },
                    )
                } else {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumSpells, key = { it.id }) { spell ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        saving = false

                                        coroutineScope.launch(Dispatchers.IO) {
                                            screenModel.saveSpell(
                                                Spell(
                                                    id = uuid4(),
                                                    compendiumId = spell.id,
                                                    name = spell.name,
                                                    range = spell.range,
                                                    target = spell.target,
                                                    duration = spell.duration,
                                                    castingNumber = spell.castingNumber,
                                                    effect = spell.effect,
                                                    memorized = false,
                                                )
                                            )

                                            onComplete()
                                        }
                                    }
                                ),
                                icon = { ItemIcon(Resources.Drawable.Spell, ItemIcon.Size.Small) },
                                text = { Text(spell.name) }
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.bodyPadding),
                onClick = onCustomSpellRequest,
            ) {
                Text(strings.buttonAddNonCompendium)
            }
        }
    }
}
