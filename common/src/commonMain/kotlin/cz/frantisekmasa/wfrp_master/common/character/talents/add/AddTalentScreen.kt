package cz.frantisekmasa.wfrp_master.common.character.talents.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.NonCompendiumTalentForm
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TimesTakenForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource

class AddTalentScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddTalentScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        val (step, setStep) = rememberSaveable {
            mutableStateOf<Step>(Step.ChoosingCompendiumTalent)
        }

        if (state == null) {
            FullScreenProgress()
            return
        }

        when (step) {
            Step.ChoosingCompendiumTalent -> {
                val navigation = LocalNavigationTransaction.current
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.talents_title_choose_compendium_talent),
                    onDismissRequest = { navigation.goBack() },
                    icon = { Resources.Drawable.Talent },
                    onSelect = { setStep(Step.FillingInTimesTaken(it.id)) },
                    onCustomItemRequest = { setStep(Step.FillingInCustomTalent) },
                    customItemButtonText = stringResource(Str.talents_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Talent,
                )
            }
            is Step.FillingInTimesTaken ->
                TimesTakenForm(
                    existingTalent = null,
                    onSave = {
                        screenModel.addCompendiumTalent(step.compendiumTalentId, timesTaken = it)
                    },
                    onDismissRequest = { setStep(Step.ChoosingCompendiumTalent) },
                )
            is Step.FillingInCustomTalent -> NonCompendiumTalentForm(
                existingTalent = null,
                onSave = screenModel::addTalent,
                onDismissRequest = { setStep(Step.ChoosingCompendiumTalent) },
            )
        }
    }

    private sealed class Step : Parcelable {

        @Parcelize
        class FillingInTimesTaken(val compendiumTalentId: Uuid) : Step()

        @Parcelize
        object ChoosingCompendiumTalent : Step()

        @Parcelize
        object FillingInCustomTalent : Step()
    }
}
