package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterTab
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun VisibleTabsSection(character: Character, screenModel: CharacterScreenModel) {
    val data = VisibleTabsFormData.fromCharacter(character)
    FormScreen(
        title = LocalStrings.current.character.titleVisibleTabs,
        formData = data,
        onSave = {
            screenModel.update { character ->
                character.updateHiddenTabs(CharacterTab.values().toSet() - it.visibleTabs)
            }
        }
    ) { validate ->

        if (validate && !data.isValid()) {
            Text(
                text = LocalStrings.current.character.errorVisibleTabRequired,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        CharacterTab.values().forEach { tab ->
            key(tab) {
                ListItem(
                    text = { Text(tab.localizedName) },
                    icon = {
                        Checkbox(
                            checked = tab in data.visibleTabs.value,
                            onCheckedChange = { checked ->
                                val visibleTabs = data.visibleTabs.value

                                data.visibleTabs.value = if (checked)
                                    visibleTabs + tab
                                else visibleTabs - tab
                            }
                        )
                    }
                )
            }
        }
    }
}

private data class VisibleTabsFormData(
    val visibleTabs: MutableState<Set<CharacterTab>>,
) : HydratedFormData<VisibleTabsData> {

    override fun isValid() = visibleTabs.value.isNotEmpty()

    override fun toValue() = VisibleTabsData(visibleTabs.value)

    companion object {
        @Composable
        fun fromCharacter(character: Character): VisibleTabsFormData {
            return VisibleTabsFormData(
                remember(character.hiddenTabs) {
                    mutableStateOf(CharacterTab.values().toSet() - character.hiddenTabs)
                }
            )
        }
    }
}

@Immutable
private data class VisibleTabsData(
    val visibleTabs: Set<CharacterTab>,
)
