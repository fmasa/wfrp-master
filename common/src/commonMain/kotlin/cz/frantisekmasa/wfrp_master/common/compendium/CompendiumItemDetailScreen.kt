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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun <A : CompendiumItem<A>> Screen.CompendiumItemDetailScreen(
    id: Uuid,
    screenModel: CompendiumItemScreenModel<A>,
    detail: @Composable (A) -> Unit,
    editDialog: @Composable (item: A, onDismissRequest: () -> Unit) -> Unit,
) {
    val itemOrError = remember { screenModel.get(id) }
        .collectWithLifecycle(null).value

    if (itemOrError == null) {
        FullScreenProgress()
        return
    }

    val navigator = LocalNavigator.currentOrThrow

    if (itemOrError !is Either.Right<A>) {
        val message = LocalStrings.current.compendium.messages.itemDoesNotExist
        val snackbarHolder = LocalPersistentSnackbarHolder.current

        LaunchedEffect(Unit) {
            snackbarHolder.showSnackbar(message)
            navigator.pop()
        }
        return
    }

    var editDialogOpened by remember { mutableStateOf(false) }

    if (editDialogOpened) {
        editDialog(itemOrError.value) { editDialogOpened = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(itemOrError.value.name) },
                actions = {
                    IconAction(
                        Icons.Rounded.Edit,
                        LocalStrings.current.commonUi.buttonEdit,
                        onClick = { editDialogOpened = true },
                    )
                },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            detail(itemOrError.value)
        }
    }
}
