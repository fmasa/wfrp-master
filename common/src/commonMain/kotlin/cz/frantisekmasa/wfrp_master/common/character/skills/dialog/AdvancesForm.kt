package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.skills.SkillRating
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun AdvancesForm(
    compendiumSkill: Skill,
    characteristics: Stats,
    onSave: suspend (advances: Int) -> AdvancesForm.SavingResult,
    isAdvanced: Boolean,
    onDismissRequest: () -> Unit,
) {
    val messageCompendiumSkillRemoved = stringResource(Str.skills_messages_compendium_skill_removed)
    val snackbarHolder = LocalPersistentSnackbarHolder.current

    val formData = AdvancesForm.Data(
        rememberSaveable(compendiumSkill.id) { mutableStateOf(1) },
    )

    FormDialog(
        title = stringResource(Str.skills_title_new),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = {
            when (onSave(it)) {
                AdvancesForm.SavingResult.SUCCESS -> {}
                AdvancesForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED -> {
                    snackbarHolder.showSnackbar(
                        messageCompendiumSkillRemoved,
                        SnackbarDuration.Short,
                    )
                }
            }

            onDismissRequest()
        }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Str.skills_label_advances),
                modifier = Modifier.weight(1f),
            )

            val minAdvances = if (isAdvanced) 1 else 0

            NumberPicker(
                value = formData.advances,
                onIncrement = { formData.advances++ },
                onDecrement = {
                    if (formData.advances > minAdvances) {
                        formData.advances--
                    }
                }
            )
        }

        SkillRating(
            label = compendiumSkill.name,
            value = characteristics.get(compendiumSkill.characteristic) + formData.advances,
            modifier = Modifier
                .padding(top = Spacing.large)
                .align(Alignment.CenterHorizontally),
        )
    }
}

object AdvancesForm {

    enum class SavingResult { SUCCESS, COMPENDIUM_ITEM_WAS_REMOVED }

    @Stable
    data class Data(
        private val advancesState: MutableState<Int>,
    ) : HydratedFormData<Int> {

        var advances by advancesState

        override fun isValid(): Boolean = true

        override fun toValue(): Int = advances
    }
}
