package cz.muni.fi.rpg.ui.gameMaster.rolls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.core.ui.forms.SelectBoxToggle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.SkillTestViewModel

@Composable
internal fun OptionsForm(
    viewModel: SkillTestViewModel,
    onDismissRequest: () -> Unit,
    selectedSkill: Skill,
    onNewSkillPickingRequest: () -> Unit,
    onExecute: (characters: Set<Character>, testModifier: Int) -> Unit,
) {
    val characters = viewModel.characters.collectWithLifecycle(null).value
    val characterIds = characters?.map { it.id }?.toSet()

    var executing by remember { mutableStateOf(false) }

    var difficulty by rememberSaveable { mutableStateOf(0) }
    var selectedCharacterIds by rememberSaveable(characterIds) {
        mutableStateOf(characterIds ?: emptySet())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onDismissRequest) },
                title = { Text(stringResource(R.string.title_skill_test)) },
                actions = {
                    TopBarAction(
                        textRes = R.string.button_test_execute,
                        enabled = !executing && (selectedCharacterIds.isNotEmpty()),
                        onClick = {
                            val charactersById =
                                characters?.associateBy { it.id } ?: return@TopBarAction

                            executing = true

                            onExecute(
                                selectedCharacterIds.map { charactersById.getValue(it) }.toSet(),
                                difficulty,
                            )
                        }
                    )
                }
            )
        }
    ) {
        if (characters == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            SelectBoxToggle(
                label = stringResource(R.string.label_skill),
                onClick = onNewSkillPickingRequest,
            ) {
                Icon(
                    painterResource(selectedSkill.characteristic.getIconId()),
                    VisualOnlyIconDescription, // TODO: Add characteristic-derived description
                    Modifier.width(24.dp)
                )
                Text(selectedSkill.name, Modifier.padding(start = Spacing.small))
            }

            SelectBox(
                label = stringResource(R.string.label_difficulty),
                value = difficulty,
                onValueChange = { difficulty = it },
                items = difficultyOptions()
            )

            CharacterList(
                characters = characters,
                selectedCharacterIds = selectedCharacterIds,
                onCharacterSelected = {
                    selectedCharacterIds = setOf(it, *selectedCharacterIds.toTypedArray())
                },
                onCharacterUnselected = {
                    selectedCharacterIds = selectedCharacterIds.filter { id -> id != it }.toSet()
                }
            )
        }
    }
}

@Composable
private fun CharacterList(
    characters: List<Character>,
    selectedCharacterIds: Set<String>,
    onCharacterSelected: (characterId: String) -> Unit,
    onCharacterUnselected: (characterId: String) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.title_characters),
            style = MaterialTheme.typography.body2,
        )

        for (character in characters) {
            val onValueChange = { checked: Boolean ->
                if (checked) {
                    onCharacterSelected(character.id)
                } else {
                    onCharacterUnselected(character.id)
                }
            }

            ListItem(
                icon = {
                    Checkbox(
                        checked = selectedCharacterIds.contains(character.id),
                        onCheckedChange = onValueChange,
                    )
                },
                modifier = Modifier.toggleable(
                    value = selectedCharacterIds.contains(character.id),
                    onValueChange = onValueChange,
                ),
                text = { Text(character.getName()) }
            )
        }
    }
}

@Composable
private fun difficultyOptions() =
    listOf(
        60 to R.string.difficulty_very_easy,
        40 to R.string.difficulty_easy,
        20 to R.string.difficulty_average,
        0 to R.string.difficulty_challenging,
        -10 to R.string.difficulty_difficult,
        -20 to R.string.difficulty_hard,
        -30 to R.string.difficulty_very_hard,
    ).map { (modifier, labelRes) ->
        modifier to stringResource(labelRes) +
            " (" + (if (modifier < 0) "" else "+") + modifier + ")"
    }
