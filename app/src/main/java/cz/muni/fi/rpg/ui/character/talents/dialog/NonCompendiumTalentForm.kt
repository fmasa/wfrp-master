package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.ui.forms.*
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
internal fun NonCompendiumTalentForm(
    viewModel: TalentsViewModel,
    existingTalent: Talent?,
    onComplete: () -> Unit,
) {
    val formData = NonCompendiumTalentFormData.fromTalent(existingTalent)
    var saving by remember { mutableStateOf(false) }
    var validate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingTalent != null)
                                R.string.title_talent_edit else
                                R.string.title_talent_new
                        )
                    )
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            if (!formData.isValid()) {
                                validate = true
                                return@SaveAction
                            }

                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true
                                viewModel.saveTalent(formData.toTalent())
                                withContext(Dispatchers.Main) { onComplete() }
                            }
                        }
                    )
                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        ScrollableColumn(
            contentPadding = PaddingValues(BodyPadding),
            verticalArrangement = Arrangement.spacedBy(FormInputVerticalPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FormInputHorizontalPadding),
                verticalAlignment = Alignment.Bottom,
            ) {
                TextInput(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.label_name),
                    value = formData.name,
                    validate = validate,
                    maxLength = Talent.NAME_MAX_LENGTH,
                )

                NumberPicker(
                    label = stringResource(R.string.label_talent_taken),
                    value = formData.taken.value,
                    onIncrement = { formData.taken.value++ },
                    onDecrement = {
                        formData.taken.value = (formData.taken.value - 1).coerceAtLeast(1)
                    }
                )
            }

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description,
                validate = validate,
                multiLine = true,
                maxLength = Talent.DESCRIPTION_MAX_LENGTH,
            )
        }
    }
}

private class NonCompendiumTalentFormData(
    val id: UUID,
    val name: InputValue,
    val description: InputValue,
    val taken: MutableState<Int>,
) : FormData {
    companion object {
        @Composable
        fun fromTalent(talent: Talent?): NonCompendiumTalentFormData = NonCompendiumTalentFormData(
            id = remember { talent?.id ?: UUID.randomUUID() },
            name = inputValue(talent?.name ?: "", Rules.NotBlank()),
            description = inputValue(talent ?. description ?: ""
        ),
            taken = savedInstanceState { talent?.taken ?: 1 },
        )
    }

    fun toTalent(): Talent = Talent(
        id = id,
        compendiumId = null,
        name = name.value,
        description = description.value,
        taken = taken.value,
    )

    override fun isValid() = name.isValid() && description.isValid() && taken.value > 0
}