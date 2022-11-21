package cz.frantisekmasa.wfrp_master.common.character.traits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TraitDetail(
    trait: Trait,
    onDismissRequest: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(remember(trait) { trait.evaluatedName }) },
                actions = actions,
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            TraitDetailBody(
                specifications = trait.specificationValues.keys,
                description = trait.description,
            )
        }
    }
}

@Composable
fun TraitDetailBody(
    specifications: Set<String>,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        if (specifications.isNotEmpty()) {
            SingleLineTextValue(
                label = LocalStrings.current.traits.labelSpecifications,
                value = remember(specifications) {
                    specifications.sorted().joinToString(", ")
                },
            )
        }

        RichText(Modifier.padding(top = 8.dp)) {
            Markdown(description)
        }
    }
}
