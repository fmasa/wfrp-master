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
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

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
            Column(Modifier.padding(Spacing.bodyPadding)) {
                Text(trait.description)
            }
        }
    }
}