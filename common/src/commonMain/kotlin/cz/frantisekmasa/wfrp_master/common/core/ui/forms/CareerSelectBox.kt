package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.defaultBackgroundColor
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import dev.icerock.moko.resources.compose.stringResource

sealed interface SelectedCareer : Parcelable {

    @Parcelize
    data class CompendiumCareer(
        val value: Character.CompendiumCareer,
        val socialStatus: SocialStatus,
    ) : SelectedCareer

    @Parcelize
    data class NonCompendiumCareer(
        val careerName: String,
        val socialClass: String,
    ) : SelectedCareer

    companion object {
        val NoCareer = NonCompendiumCareer("", "")
    }
}

@Composable
fun CareerSelectBox(
    careers: List<Career>,
    value: SelectedCareer?,
    onValueChange: (SelectedCareer) -> Unit,
) {
    val items = remember(careers) {
        careers
            .flatMap { career -> career.levels.map { it to career } }
            .map { careerLevelName(it.first, it.second) }
            .sortedBy { it.name }
            .map { it to it.name }
    }

    val itemValue by derivedStateOf {
        when (value) {
            is SelectedCareer.CompendiumCareer -> items.firstOrNull { (item, _) ->
                item.careerId == value.value.careerId && item.levelId == value.value.levelId
            }?.first?.name
            SelectedCareer.NoCareer -> null
            is SelectedCareer.NonCompendiumCareer -> value.careerName.takeIf { it != "" }
                ?: value.socialClass
            else -> null
        }
    }

    var dialogOpened by rememberSaveable { mutableStateOf(false) }

    if (dialogOpened) {
        CareerChooserDialog(
            onDismissRequest = { dialogOpened = false },
            items = items,
            onChoose = onValueChange,
            currentValue = value,
        )
    }

    Column {
        SelectBoxLabel(stringResource(Str.character_label_career))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                SelectBoxToggle(
                    label = null,
                    onClick = { dialogOpened = true },
                ) {
                    Text(
                        itemValue ?: stringResource(Str.common_ui_item_none),
                        maxLines = 1,
                    )
                }
            }

            if (itemValue != null) {
                CloseButton(
                    contentDescription = stringResource(Str.careers_button_clear_select_box),
                    onClick = {
                        onValueChange(SelectedCareer.NoCareer)
                    }
                )
            }
        }
    }
}

@Composable
private fun CareerChooserDialog(
    items: List<Pair<CareerLevel, String>>,
    currentValue: SelectedCareer?,
    onChoose: (SelectedCareer) -> Unit,
    onDismissRequest: () -> Unit,
) {

    FullScreenDialog(
        onDismissRequest = onDismissRequest,
    ) {
        var customCareer by rememberSaveable {
            mutableStateOf(
                currentValue is SelectedCareer.NonCompendiumCareer &&
                    currentValue != SelectedCareer.NoCareer
            )
        }

        if (customCareer) {
            NonCompendiumCareerForm(
                onDismissRequest = onDismissRequest,
                defaultValue = currentValue as? SelectedCareer.NonCompendiumCareer,
                onSubmit = {
                    onChoose(it)
                    onDismissRequest()
                }
            )

            return@FullScreenDialog
        }

        Column(Modifier.fillMaxSize()) {
            SearchableList(
                modifier = Modifier.weight(1f),
                data = SearchableList.Data.Loaded(items),
                searchableValue = { it.second },
                navigationIcon = { CloseButton(onDismissRequest) },
                title = stringResource(Str.compendium_title_careers),
                searchPlaceholder = stringResource(Str.careers_search_placeholder),
                emptyUi = {
                    EmptyUI(
                        text = stringResource(
                            Str.character_creation_messages_no_careers_in_compendium,
                        ),
                        subText = stringResource(
                            Str.compendium_messages_no_items_in_compendium_subtext_player,
                        ),
                        icon = Resources.Drawable.Career,
                    )
                },
                key = { it.first.levelId },
            ) { (level, label) ->
                ListItem(
                    modifier = Modifier.clickable(
                        onClick = {
                            onChoose(
                                SelectedCareer.CompendiumCareer(
                                    Character.CompendiumCareer(
                                        careerId = level.careerId,
                                        levelId = level.levelId,
                                    ),
                                    level.socialStatus,
                                )
                            )
                            onDismissRequest()
                        }
                    ),
                    icon = {
                        ItemIcon(
                            Resources.Drawable.Career,
                            ItemIcon.Size.Small,
                            backgroundColor = if (
                                currentValue is SelectedCareer.CompendiumCareer &&
                                currentValue.value.levelId == level.levelId
                            ) MaterialTheme.colors.primaryVariant else defaultBackgroundColor()
                        )
                    },
                    text = { Text(label) },
                )
            }

            Surface(elevation = 8.dp) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.medium),
                    onClick = { customCareer = true },
                ) {
                    Text(stringResource(Str.character_label_custom_career))
                }
            }
        }
    }
}

@Composable
fun NonCompendiumCareerForm(
    onDismissRequest: () -> Unit,
    defaultValue: SelectedCareer.NonCompendiumCareer?,
    onSubmit: (SelectedCareer.NonCompendiumCareer) -> Unit,
) {
    val careerName = inputValue(defaultValue?.careerName ?: "")
    val socialClass = inputValue(defaultValue?.socialClass ?: "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Str.character_label_career))
                },
                navigationIcon = { BackButton(onDismissRequest) },
                actions = {
                    SaveAction(
                        onClick = {
                            onSubmit(
                                SelectedCareer.NonCompendiumCareer(
                                    careerName = careerName.value,
                                    socialClass = socialClass.value,
                                )
                            )
                        }
                    )
                }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            TextInput(
                label = stringResource(Str.character_label_class),
                value = socialClass,
                maxLength = Character.SOCIAL_CLASS_MAX_LENGTH,
                validate = true,
            )

            TextInput(
                label = stringResource(Str.character_label_career),
                value = careerName,
                maxLength = Character.CAREER_MAX_LENGTH,
                validate = true,
            )
        }
    }
}

@Immutable
private data class CareerLevel(
    val levelId: Uuid,
    val careerId: Uuid,
    val name: String,
    val socialStatus: SocialStatus,
)

private fun careerLevelName(level: Career.Level, career: Career): CareerLevel {
    val levelNumber = career.levels.indexOfFirst { it.id == level.id } + 1

    return CareerLevel(
        careerId = career.id,
        levelId = level.id,
        name = "${level.name} (${career.name} $levelNumber)",
        socialStatus = level.status,
    )
}
