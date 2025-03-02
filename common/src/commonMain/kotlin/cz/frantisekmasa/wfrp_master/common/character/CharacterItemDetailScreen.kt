package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import arrow.core.Either
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow

abstract class CharacterItemDetailScreen(
    protected val characterId: CharacterId,
    private val itemId: Uuid,
) : Screen {
    @Composable
    protected fun <T : CharacterItem<T, *>> Detail(
        screenModel: CharacterItemScreenModel<T>,
        content: @Composable (T, isGameMaster: Boolean) -> Unit,
    ) {
        Detail(
            itemFlow = remember(screenModel) { screenModel.getItem(itemId) },
            isGameMasterFlow = screenModel.isGameMaster,
            content = content,
        )
    }

    @Composable
    protected fun <T> Detail(
        itemFlow: Flow<Either<CompendiumItemNotFound, T>>,
        isGameMasterFlow: Flow<Boolean>,
        content: @Composable (T, isGameMaster: Boolean) -> Unit,
    ) {
        val isGameMaster = isGameMasterFlow.collectWithLifecycle(null).value
        val itemOrError =
            itemFlow.collectWithLifecycle(null)
                .value

        if (itemOrError == null || isGameMaster == null) {
            Surface {
                FullScreenProgress()
            }
            return
        }

        val navigation = LocalNavigationTransaction.current
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val item = itemOrError.orNull()

        if (item == null) {
            val message = stringResource(Str.common_ui_item_does_not_exist)

            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(message)
                navigation.goBack()
            }

            return
        }

        content(item, isGameMaster)
    }
}
