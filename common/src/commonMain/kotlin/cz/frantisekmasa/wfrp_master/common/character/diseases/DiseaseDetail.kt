package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DiseaseDetail(
    disease: Disease,
    subheadBar: @Composable ColumnScope.() -> Unit,
    onEditRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    isGameMaster: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(disease.name) },
                actions = {
                    IconAction(
                        Icons.Rounded.Edit,
                        stringResource(Str.character_title_edit),
                        onClick = onEditRequest,
                    )
                },
            )
        },
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            subheadBar()
            DiseaseDetailBody(
                subheadBar = {
                    if (isGameMaster) {
                        SingleLineTextValue(
                            label = stringResource(Str.diseases_label_diagnosed),
                            value =
                                stringResource(
                                    if (disease.isDiagnosed) {
                                        Str.common_ui_boolean_yes
                                    } else {
                                        Str.common_ui_boolean_no
                                    },
                                ),
                        )
                    }
                },
                symptoms = disease.symptoms,
                permanentEffects = disease.permanentEffects,
                description = disease.description,
            )
        }
    }
}

@Composable
fun DiseaseDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    symptoms: List<String>,
    permanentEffects: String,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        subheadBar()

        SingleLineTextValue(
            label = stringResource(Str.diseases_label_symptoms),
            value = symptoms.joinToString(", "),
        )

        SingleLineTextValue(
            label = stringResource(Str.diseases_label_permanent_effects),
            value = permanentEffects,
        )

        RichText(Modifier.padding(top = Spacing.small)) {
            Markdown(description)
        }
    }
}
