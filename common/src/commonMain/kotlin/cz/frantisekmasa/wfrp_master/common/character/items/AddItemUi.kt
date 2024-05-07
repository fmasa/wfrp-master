package cz.frantisekmasa.wfrp_master.common.character.items

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.NavigationTransaction
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <A : CharacterItem<A, B>, B : CompendiumItem<B>> rememberAddItemUiState(
    saver: suspend (A) -> Unit,
    detailScreenFactory: (A) -> Screen,
): AddItemUiState<A, B> {
    val snackbarHostState = remember { SnackbarHostState() }
    val step =
        rememberSaveable {
            mutableStateOf<AddItemUiState.Step<B>>(AddItemUiState.Step.ChoosingCompendiumItems)
        }
    val successMessageText = stringResource(Str.common_ui_item_added)
    val detailButtonText = stringResource(Str.common_ui_button_open)
    val coroutineScope = rememberCoroutineScope()
    val navigation = LocalNavigationTransaction.current

    return remember {
        AddItemUiState(
            step,
            saver,
            detailScreenFactory,
            detailButtonText = detailButtonText,
            successMessageText = successMessageText,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            navigation = navigation,
        )
    }
}

@Stable
class AddItemUiState<A : CharacterItem<A, B>, B : CompendiumItem<B>>(
    private val _step: MutableState<Step<B>>,
    private val saver: suspend (A) -> Unit,
    val detailScreenFactory: (A) -> Screen,
    private val detailButtonText: String,
    private val successMessageText: String,
    val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope,
    private val navigation: NavigationTransaction,
) {
    val step: Step<B> @Composable get() = _step.value

    fun openNonCompendiumItemForm() {
        _step.value = Step.CreatingNonCompendiumItem
    }

    fun openChoosingScreen() {
        _step.value = Step.ChoosingCompendiumItems
    }

    fun openSpecificationScreen(item: B) {
        _step.value = Step.CompendiumItemSpecification(item)
    }

    suspend fun saveItem(item: A) {
        saver(item)
        coroutineScope.launch {
            val result =
                snackbarHostState.showSnackbar(
                    message = successMessageText,
                    actionLabel = detailButtonText,
                )

            if (result == SnackbarResult.ActionPerformed) {
                navigation.replace(detailScreenFactory(item))
            }
        }
    }

    sealed interface Step<out T : CompendiumItem<out T>> : Parcelable {
        @Parcelize
        object ChoosingCompendiumItems : Step<Nothing>

        @Parcelize
        object CreatingNonCompendiumItem : Step<Nothing>

        @Parcelize
        data class CompendiumItemSpecification<T : CompendiumItem<T>>(val item: T) : Step<T>
    }
}

@Composable
fun <A : CharacterItem<A, B>, B : CompendiumItem<B>> AddItemUi(
    state: AddItemUiState<A, B>,
    chooser: @Composable () -> Unit,
    specification: @Composable (compendiumItem: B) -> Unit,
    nonCompendiumItemForm: @Composable () -> Unit,
) {
    Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = state.snackbarHostState)) {
        when (val step = state.step) {
            AddItemUiState.Step.ChoosingCompendiumItems -> {
                chooser()
            }

            AddItemUiState.Step.CreatingNonCompendiumItem -> {
                nonCompendiumItemForm()
            }

            is AddItemUiState.Step.CompendiumItemSpecification -> {
                specification(step.item)
            }
        }
    }
}
