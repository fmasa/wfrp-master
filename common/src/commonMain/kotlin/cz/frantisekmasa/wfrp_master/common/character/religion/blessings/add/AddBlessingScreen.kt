package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog.NonCompendiumBlessingForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

class AddBlessingScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddBlessingScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        val (step, setStep) = rememberSaveable {
            mutableStateOf<Step>(Step.ChoosingCompendiumMiracle)
        }

        if (state == null) {
            FullScreenProgress()
            return
        }

        when (step) {
            Step.ChoosingCompendiumMiracle -> {
                val navigation = LocalNavigationTransaction.current

                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.blessings_title_choose_compendium_blessing),
                    onDismissRequest = navigation::goBack,
                    icon = { Resources.Drawable.Blessing },
                    onSelect = { screenModel.addBlessing(Blessing.fromCompendium(it)) },
                    onCustomItemRequest = { setStep(Step.FillingInCustomBlessing) },
                    customItemButtonText = stringResource(Str.blessings_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Blessing,
                )
            }

            Step.FillingInCustomBlessing -> NonCompendiumBlessingForm(
                existingBlessing = null,
                onDismissRequest = { setStep(Step.ChoosingCompendiumMiracle) },
                onSave = screenModel::addBlessing,
            )
        }
    }

    private sealed class Step : Parcelable {
        @Parcelize
        object ChoosingCompendiumMiracle : Step()

        @Parcelize
        object FillingInCustomBlessing : Step()
    }
}
