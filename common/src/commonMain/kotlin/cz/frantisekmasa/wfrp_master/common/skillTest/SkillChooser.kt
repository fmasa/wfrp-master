package cz.frantisekmasa.wfrp_master.common.skillTest

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
internal fun SkillChooser(
    screenModel: SkillTestScreenModel,
    onDismissRequest: () -> Unit,
    onSkillSelected: (Skill) -> Unit
) {
    val strings = LocalStrings.current.skills

    val skills by screenModel.skills.collectWithLifecycle(null)
    val data by derivedStateOf {
        skills?.let { SearchableList.Data.Loaded(it) }
            ?: SearchableList.Data.Loading
    }

    SearchableList(
        data = data,
        navigationIcon = { CloseButton(onClick = onDismissRequest) },
        title = strings.titleSelectSkill,
        key = { it.id },
        searchableValue = { it.name },
        searchPlaceholder = LocalStrings.current.compendium.searchPlaceholder,
        emptyUi = {
            EmptyUI(
                icon = Resources.Drawable.Skill,
                text = strings.messages.noSkillsInCompendium,
            )
        },
    ) { skill ->
        ListItem(
            modifier = Modifier.clickable(onClick = { onSkillSelected(skill) }),
            icon = { ItemIcon(skill.characteristic.getIcon(), ItemIcon.Size.Small) },
            text = { Text(skill.name) }
        )
    }
}
