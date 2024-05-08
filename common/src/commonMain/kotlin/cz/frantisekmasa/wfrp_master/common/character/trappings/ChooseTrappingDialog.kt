package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChooseTrappingDialog(
    title: String,
    trappings: List<TrappingItem>,
    onSelected: suspend (TrappingItem) -> Unit,
    emptyUiText: String,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest) {
        var saving by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        SearchableList(
            data = SearchableList.Data.Loaded(trappings),
            key = { it.item.id },
            searchableValue = { it.item.name },
            navigationIcon = { CloseButton(onDismissRequest) },
            title = title,
            searchPlaceholder = stringResource(Str.trappings_search_placeholder),
            emptyUi = {
                EmptyUI(
                    text = emptyUiText,
                    icon = Resources.Drawable.TrappingContainer,
                )
            },
        ) { trapping ->
            TrappingItem(
                trapping,
                modifier =
                    Modifier.clickable(
                        onClick = {
                            saving = false
                            coroutineScope.launch(Dispatchers.IO) {
                                onSelected(trapping)
                                onDismissRequest()
                            }
                        },
                    ),
            )
        }
    }
}
