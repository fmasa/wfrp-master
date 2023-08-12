package cz.frantisekmasa.wfrp_master.common.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.SelectionDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun LanguageDialog(
    selected: Language,
    onSelect: (Language) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SelectionDialog(
        title = stringResource(Str.settings_language),
        items = Language.values().toList(),
        selected = selected,
        onDismissRequest = onDismissRequest,
        onSelect = onSelect
    ) { language ->
        Column {
            Text(language.localizedName)
            Text(language.englishName, style = MaterialTheme.typography.caption)
        }
    }
}
