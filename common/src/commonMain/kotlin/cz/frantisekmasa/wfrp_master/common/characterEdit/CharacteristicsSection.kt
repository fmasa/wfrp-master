package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCharacteristicsForm
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CharacteristicsSection(character: Character, screenModel: CharacterScreenModel) {
    val formData = CharacterCharacteristicsForm.Data.fromCharacter(character)

    FormScreen(
        title = stringResource(Str.character_title_characteristics),
        formData = formData,
        onSave = { data ->
            screenModel.update { it.updateCharacteristics(data.base, data.advances) }
        }
    ) { validate ->
        CharacterCharacteristicsForm(formData, validate)
    }
}
