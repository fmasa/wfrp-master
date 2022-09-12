package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CareerSelectBox(
    careers: List<Career>,
    value: Character.CompendiumCareer?,
    onValueChange: (Character.CompendiumCareer?) -> Unit,
    validate: Boolean,
) {
    val none = LocalStrings.current.commonUi.itemNone

    val items = remember(careers, none) {
        careers
            .flatMap { career -> career.levels.map { it to career } }
            .map { careerLevelName(it.first, it.second) }
            .sortedBy { it.name }
            .map { it to it.name } + Pair(null, none)
    }

    val itemValue by derivedStateOf {
        items.firstOrNull { (item, _) ->
            (value == null && item == null) ||
                (
                    value != null && item != null &&
                        item.careerId == value.careerId &&
                        item.levelId == value.levelId
                    )
        }?.first
    }

    SelectBox(
        label = LocalStrings.current.character.labelCareer,
        items = items,
        onValueChange = { selectedValue ->
            onValueChange(
                selectedValue?.let {
                    Character.CompendiumCareer(
                        careerId = it.careerId,
                        levelId = it.levelId,
                    )
                }
            )
        },
        value = itemValue,
    )

    if (validate && itemValue == null) {
        ErrorMessage(LocalStrings.current.validation.notBlank)
    }
}

@Immutable
private data class CareerLevel(
    val levelId: Uuid,
    val careerId: Uuid,
    val name: String,
)

private fun careerLevelName(level: Career.Level, career: Career): CareerLevel {
    val levelNumber = career.levels.indexOfFirst { it.id == level.id } + 1

    return CareerLevel(
        careerId = career.id,
        levelId = level.id,
        name = "${level.name} (${career.name} $levelNumber)"
    )
}
