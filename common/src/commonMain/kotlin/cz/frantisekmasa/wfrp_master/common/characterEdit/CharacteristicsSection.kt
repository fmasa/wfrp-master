package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCharacteristicsForm
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CharacteristicsSection(character: Character, screenModel: CharacterScreenModel) {
    val formData = CharacterCharacteristicsForm.Data.fromCharacter(character)

    FormScreen(
        title = LocalStrings.current.character.titleCharacteristics,
        formData = formData,
        onSave = { data ->
            screenModel.update { it.updateCharacteristics(data.base, data.advances) }
        }
    ) { validate ->
        CharacterCharacteristicsForm(formData, validate)
    }
}