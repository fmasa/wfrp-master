package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.composables.BodyPadding
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
internal fun CompendiumSpellChooser(
    viewModel: SpellsViewModel,
    onComplete: () -> Unit,
    onCustomSpellRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_choose_compendium_spell)) })
        }
    ) {
        val compendiumSpells = viewModel.notUsedSpellsFromCompendium.observeAsState().value
        val totalCompendiumSpellCount = viewModel.compendiumSpellsCount.observeAsState().value
        var saving by remember { mutableStateOf(false) }

        if (compendiumSpells == null || totalCompendiumSpellCount == null || saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumSpells.isEmpty()) {
                    EmptyUI(
                        drawableResourceId = R.drawable.ic_spells,
                        textId = R.string.no_spells_in_compendium,
                        subTextId = if (totalCompendiumSpellCount == 0)
                            R.string.no_spells_in_compendium_sub_text_player
                        else null,
                    )
                } else {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(contentPadding = PaddingValues(BodyPadding)) {
                        items(compendiumSpells) { spell ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        saving = false

                                        coroutineScope.launch(Dispatchers.IO) {
                                            viewModel.saveSpell(
                                                Spell(
                                                    id = UUID.randomUUID(),
                                                    compendiumId = spell.id,
                                                    name = spell.name,
                                                    range = spell.range,
                                                    target = spell.target,
                                                    duration = spell.duration,
                                                    castingNumber = spell.castingNumber,
                                                    effect = spell.effect,
                                                )
                                            )

                                            withContext(Dispatchers.Main) { onComplete() }
                                        }
                                    }
                                ),
                                icon = { ItemIcon(R.drawable.ic_spells, ItemIcon.Size.Small) },
                                text = { Text(spell.name) }
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BodyPadding),
                onClick = onCustomSpellRequest,
            ) {
                Text(stringResource(R.string.button_add_non_compendium_spell))
            }
        }
    }
}