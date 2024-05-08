package cz.frantisekmasa.wfrp_master.common.skillTest

import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun SkillChooser(
    screenModel: SkillTestScreenModel,
    onDismissRequest: () -> Unit,
    onSkillSelected: (Skill) -> Unit,
) {
    val skills by screenModel.skills.collectWithLifecycle(null)
    val data by derivedStateOf {
        skills?.let { SearchableList.Data.Loaded(it) }
            ?: SearchableList.Data.Loading
    }

    SearchableList(
        data = data,
        navigationIcon = { CloseButton(onClick = onDismissRequest) },
        title = stringResource(Str.skills_title_select_skill),
        key = { it.id },
        searchableValue = { it.name },
        searchPlaceholder = stringResource(Str.compendium_search_placeholder),
        emptyUi = {
            EmptyUI(
                icon = Resources.Drawable.Skill,
                text = stringResource(Str.skills_messages_no_skills_in_compendium),
            )
        },
    ) { skill ->
        ListItem(
            modifier = Modifier.clickable(onClick = { onSkillSelected(skill) }),
            icon = { ItemIcon(skill.characteristic.getIcon(), ItemIcon.Size.Small) },
            text = { Text(skill.name) },
        )
    }
}
