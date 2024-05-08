package cz.frantisekmasa.wfrp_master.common.character.skills.addBasic

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AddBasicSkillsScreen(
    private val characterId: CharacterId,
) : Screen {
    object FormData : HydratedFormData<Unit> {
        override fun isValid(): Boolean = true

        override fun toValue(): Unit = Unit
    }

    @Composable
    override fun Content() {
        val screenModel: AddBasicSkillsScreenModel = rememberScreenModel(arg = characterId)
        val state = screenModel.state.collectWithLifecycle(null).value

        if (state == null) {
            FullScreenProgress()
            return
        }

        FormScreen(
            title = stringResource(Str.skills_add_basic_skills_dialog_title),
            formData = FormData,
            onSave = { screenModel.addBasicSkills() },
            enabled = state.basicSkillsCount > 0,
        ) {
            if (state.basicSkillsCount == 0) {
                Text(stringResource(Str.skills_messages_no_basic_skills_to_add))
            } else {
                Text(
                    stringResource(
                        Str.skills_messages_add_basic_skills_explanation,
                        state.basicSkillsCount,
                    ),
                )
            }
        }
    }
}
