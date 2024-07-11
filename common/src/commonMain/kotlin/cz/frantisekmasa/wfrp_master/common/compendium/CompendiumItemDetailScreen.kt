package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import arrow.core.Either
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun <A : CompendiumItem<A>> Screen.CompendiumItemDetailScreen(
    id: Uuid,
    screenModel: CompendiumItemScreenModel<A>,
    scrollable: Boolean = true,
    detail: @Composable (A) -> Unit,
    editDialog: @Composable (item: A, onDismissRequest: () -> Unit) -> Unit,
) {
    CompendiumItemDetailScreen(
        itemFlow =
            remember {
                screenModel.get(id).map { either ->
                    either.map {
                        DefaultCompendiumItemDetailScreenState(it)
                    }
                }
            },
        scrollable = scrollable,
        detail = { detail(it.item) },
        editDialog = editDialog,
        screenModel = screenModel,
    )
}

interface CompendiumItemDetailScreenState<T : CompendiumItem<T>> {
    val item: T
}

private data class DefaultCompendiumItemDetailScreenState<T : CompendiumItem<T>>(
    override val item: T,
) : CompendiumItemDetailScreenState<T>

@Composable
fun <T : CompendiumItem<T>, A : CompendiumItemDetailScreenState<T>> Screen.CompendiumItemDetailScreen(
    itemFlow: Flow<Either<CompendiumItemNotFound, A>>,
    scrollable: Boolean = true,
    screenModel: CompendiumItemScreenModel<T>,
    detail: @Composable (A) -> Unit,
    editDialog: @Composable (item: T, onDismissRequest: () -> Unit) -> Unit,
) {
    val itemOrError = itemFlow.collectWithLifecycle(null).value

    if (itemOrError == null) {
        FullScreenProgress()
        return
    }

    val navigation = LocalNavigationTransaction.current

    val item = itemOrError.orNull()

    if (item == null) {
        val message = stringResource(Str.common_ui_item_does_not_exist)
        val snackbarHolder = LocalPersistentSnackbarHolder.current

        LaunchedEffect(Unit) {
            snackbarHolder.showSnackbar(message)
            navigation.goBack()
        }
        return
    }

    var editDialogOpened by remember { mutableStateOf(false) }

    if (editDialogOpened) {
        editDialog(item.item) { editDialogOpened = false }
    }

    val compendiumItem = item.item

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(compendiumItem.name) },
                actions = {
                    IconAction(
                        Icons.Rounded.Edit,
                        stringResource(Str.common_ui_button_edit),
                        onClick = { editDialogOpened = true },
                    )
                },
            )
        },
    ) {
        Column(
            if (scrollable) {
                Modifier.verticalScroll(rememberScrollState())
            } else {
                Modifier
            },
        ) {
            VisibilitySwitchBar(
                visible = compendiumItem.isVisibleToPlayers,
                onChange = { screenModel.changeVisibility(compendiumItem, it) },
            )

            detail(item)
        }
    }
}
