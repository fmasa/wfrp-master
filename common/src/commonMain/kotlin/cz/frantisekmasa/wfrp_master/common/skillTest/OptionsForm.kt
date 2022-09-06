package cz.frantisekmasa.wfrp_master.common.skillTest

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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBoxToggle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlin.math.absoluteValue

@Composable
internal fun OptionsForm(
    screenModel: SkillTestScreenModel,
    onDismissRequest: () -> Unit,
    selectedSkill: Skill,
    onNewSkillPickingRequest: () -> Unit,
    onExecute: (characters: Set<Character>, testModifier: Int) -> Unit,
) {
    val characters = screenModel.characters.collectWithLifecycle(null).value
    val characterIds = characters?.map { it.id }?.toSet()

    var executing by remember { mutableStateOf(false) }

    var difficulty by rememberSaveable { mutableStateOf(0) }
    var selectedCharacterIds by rememberSaveable(characterIds) {
        mutableStateOf(characterIds ?: emptySet())
    }

    val strings = LocalStrings.current.tests

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onDismissRequest) },
                title = { Text(strings.titleSkillTest) },
                actions = {
                    TopBarAction(
                        text = strings.buttonExecute,
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
                label = strings.labelSkill,
                onClick = onNewSkillPickingRequest,
            ) {
                Icon(
                    drawableResource(selectedSkill.characteristic.getIcon()),
                    VisualOnlyIconDescription, // TODO: Add characteristic-derived description
                    Modifier.width(24.dp)
                )
                Text(selectedSkill.name, Modifier.padding(start = Spacing.small))
            }

            SelectBox(
                label = strings.labelDifficulty,
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
            LocalStrings.current.parties.titleCharacters,
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
                text = { Text(character.name) }
            )
        }
    }
}

@Composable
private fun difficultyOptions(): List<Pair<Int, String>> {
    val labels = LocalStrings.current.tests.difficulties

    return listOf(
        60 to labels.veryEasy,
        40 to labels.easy,
        20 to labels.average,
        0 to labels.challenging,
        -10 to labels.difficult,
        -20 to labels.hard,
        -30 to labels.veryHard,
    ).map { (modifier, label) ->
        modifier to "$label (${modifier.signSymbol}${modifier.absoluteValue})"
    }
}

private val Int.signSymbol get() = when {
    this < 0 -> "-"
    else -> "+"
}
