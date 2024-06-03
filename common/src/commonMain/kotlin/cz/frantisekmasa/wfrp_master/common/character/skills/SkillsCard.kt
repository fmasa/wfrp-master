package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.skills.add.AddSkillScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.addBasic.AddBasicSkillsScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.StickyHeader
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.skillsCard(
    characterId: CharacterId,
    skills: ImmutableList<SkillDataItem>,
    onRemove: (SkillDataItem) -> Unit,
) {
    stickyHeader(key = "skills-header") {
        StickyHeader {
            CardTitle(
                stringResource(Str.skills_title_skills),
                actions = {
                    var contextMenuExpanded by remember { mutableStateOf(false) }
                    val navigation = LocalNavigationTransaction.current

                    IconButton(
                        onClick = { navigation.navigate(AddSkillScreen(characterId)) },
                    ) {
                        Icon(Icons.Rounded.Add, stringResource(Str.skills_title_add))
                    }

                    IconButton(onClick = { contextMenuExpanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            stringResource(Str.common_ui_label_open_context_menu),
                        )
                    }

                    DropdownMenu(
                        expanded = contextMenuExpanded,
                        onDismissRequest = { contextMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                contextMenuExpanded = false
                                navigation.navigate(AddBasicSkillsScreen(characterId))
                            },
                        ) {
                            Text(stringResource(Str.skills_button_import_basic_skills))
                        }
                    }
                },
            )
        }
    }

    if (skills.isEmpty()) {
        item(key = "skills-empty-ui") {
            if (skills.isEmpty()) {
                EmptyUI(
                    text = stringResource(Str.skills_messages_character_has_no_skills),
                    icon = Resources.Drawable.Skill,
                    size = EmptyUI.Size.Small,
                )
            }
        }
    }

    itemsIndexed(skills, key = { _, it -> "skill" to it.id }) { index, skill ->
        val navigation = LocalNavigationTransaction.current

        SkillItem(
            skill,
            onClick = {
                navigation.navigate(
                    CharacterSkillDetailScreen(
                        characterId,
                        skill.id,
                    ),
                )
            },
            onRemove = { onRemove(skill) },
            showDivider = index != 0,
        )
    }
}

@Composable
private fun SkillItem(
    skill: SkillDataItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    showDivider: Boolean,
) {
    Column(Modifier.padding(horizontal = Spacing.large)) {
        if (showDivider) {
            Divider()
        }

        WithContextMenu(
            items =
                listOf(
                    ContextMenu.Item(
                        stringResource(Str.common_ui_button_remove),
                        onClick = { onRemove() },
                    ),
                ),
            onClick = onClick,
        ) {
            ListItem(
                text = {
                    Text(
                        skill.name,
                        fontWeight =
                            if (skill.advances > 0) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                    )
                },
                trailing = { TestNumber(skill.testNumber) },
            )
        }
    }
}

@Composable
private fun TestNumber(testNumber: Int) {
    Row {
        Text(testNumber.toString(), Modifier.padding(start = 4.dp))
    }
}

data class SkillDataItem(
    val id: Uuid,
    val name: String,
    val advances: Int,
    val testNumber: Int,
)
