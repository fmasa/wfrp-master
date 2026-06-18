package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import cz.frantisekmasa.wfrp_master.common.character.characterItemsCard
import cz.frantisekmasa.wfrp_master.common.character.skills.add.AddSkillScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.addBasic.AddBasicSkillsScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.skillsCard(
    characterId: CharacterId,
    skills: ImmutableList<SkillDataItem>,
    onRemove: (SkillDataItem) -> Unit,
) {
    characterItemsCard(
        title = { stringResource(Str.skills_title_skills) },
        key = "skills",
        id = SkillDataItem::id,
        items = skills,
        newItemScreen = { AddSkillScreen(characterId) },
        noItems = {
            EmptyUI(
                text = stringResource(Str.skills_messages_character_has_no_skills),
                icon = Resources.Drawable.Skill,
                size = EmptyUI.Size.Small,
            )
        },
        actions = {
            var contextMenuExpanded by remember { mutableStateOf(false) }
            val navigation = LocalNavigationTransaction.current

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
        detailScreen = { skill -> CharacterSkillDetailScreen(characterId, skill.id) },
        onRemove = onRemove,
        item = { skill -> SkillItem(skill) },
    )
}

@Composable
private fun SkillItem(skill: SkillDataItem) {
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
