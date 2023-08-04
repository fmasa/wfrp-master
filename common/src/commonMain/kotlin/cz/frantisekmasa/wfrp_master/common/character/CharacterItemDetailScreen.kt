package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

abstract class CharacterItemDetailScreen(
    protected val characterId: CharacterId,
    private val itemId: Uuid,
) : Screen {

    @Composable
    protected fun <T : CharacterItem<T, *>> Detail(
        screenModel: CharacterItemScreenModel<T, *>,
        content: @Composable (T, isGameMaster: Boolean) -> Unit,
    ) {
        val items = screenModel.items.collectWithLifecycle(null).value
        val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(null).value

        if (items == null || isGameMaster == null) {
            Surface {
                FullScreenProgress()
            }
            return
        }

        val navigation = LocalNavigationTransaction.current
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val item = remember(items, itemId) { items.firstOrNull { it.id == itemId } }

        if (item == null) {
            val message = LocalStrings.current.commonUi.itemDoesNotExist

            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(message)
                navigation.goBack()
            }

            return
        }

        content(item, isGameMaster = isGameMaster)
    }
}
