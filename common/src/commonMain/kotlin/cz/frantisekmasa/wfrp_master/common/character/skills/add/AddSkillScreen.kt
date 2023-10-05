package cz.frantisekmasa.wfrp_master.common.character.skills.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CompendiumItemChooser
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.AdvancesForm
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.NonCompendiumSkillForm
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class AddSkillScreen(
    private val characterId: CharacterId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: AddSkillScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        val (step, setStep) = rememberSaveable {
            mutableStateOf<Step>(Step.ChoosingCompendiumSkill)
        }

        if (state == null) {
            FullScreenProgress()
            return
        }

        val navigation = LocalNavigationTransaction.current

        when (step) {
            Step.ChoosingCompendiumSkill -> {
                CompendiumItemChooser(
                    state = state.availableCompendiumItems,
                    title = stringResource(Str.skills_title_choose_compendium_skill),
                    onDismissRequest = { navigation.goBack() },
                    icon = { it.characteristic.getIcon() },
                    onSelect = { setStep(Step.FillingInAdvances(it, it.advanced)) },
                    onCustomItemRequest = { setStep(Step.FillingInCustomSkill) },
                    customItemButtonText = stringResource(Str.skills_button_add_non_compendium),
                    emptyUiIcon = Resources.Drawable.Skill,
                )
            }
            is Step.FillingInAdvances ->
                AdvancesForm(
                    compendiumSkill = step.compendiumSkill,
                    characteristics = state.characteristics,
                    isAdvanced = step.isAdvanced,
                    onDismissRequest = { navigation.goBack() },
                    onSave = { screenModel.addCompendiumSkill(step.compendiumSkill.id, it) }
                )
            is Step.FillingInCustomSkill -> NonCompendiumSkillForm(
                onSave = screenModel::addCustomSkill,
                existingSkill = null,
                characteristics = state.characteristics,
                onDismissRequest = { setStep(Step.ChoosingCompendiumSkill) },
            )
        }
    }

    private sealed class Step : Parcelable {
        @Parcelize
        class FillingInAdvances(
            val compendiumSkill: CompendiumSkill,
            val isAdvanced: Boolean,
        ) : Step()

        @Parcelize
        object ChoosingCompendiumSkill : Step()

        @Parcelize
        object FillingInCustomSkill : Step()
    }
}
