package cz.frantisekmasa.wfrp_master.common.compendium.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun JournalEntryDialog(
    entry: JournalEntry?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (JournalEntry) -> Unit,
) {
    val formData = JournalEntryFormData.fromEntry(entry)

    CompendiumItemDialog(
        title = stringResource(
            if (entry == null)
                Str.skills_title_new
            else Str.skills_title_edit
        ),
        formData = formData,
        saver = onSaveRequest,
        onDismissRequest = onDismissRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(Str.journal_label_entry_name),
                value = formData.name,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(Str.journal_label_parents),
                value = formData.parents,
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH,
            )

            Text(
                stringResource(
                    Str.journal_parents_helper_text,
                    JournalEntry.PARENT_SEPARATOR,
                ),
                style = MaterialTheme.typography.body2,
            )

            TextInput(
                label = stringResource(Str.journal_label_text),
                value = formData.text,
                validate = validate,
                maxLength = JournalEntry.TEXT_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )

            TextInput(
                label = stringResource(Str.journal_label_gm_text),
                value = formData.gmText,
                validate = validate,
                maxLength = JournalEntry.TEXT_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )
        }
    }
}

@Stable
private data class JournalEntryFormData(
    val id: Uuid,
    val name: InputValue,
    val parents: InputValue,
    val text: InputValue,
    val gmText: InputValue,
    val isPinned: Boolean,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<JournalEntry> {
    override fun toValue() = JournalEntry(
        id = id,
        name = name.value,
        parents = parents.value
            .split(JournalEntry.PARENT_SEPARATOR)
            .filter { it.isNotBlank() }
            .map { it.trim() },
        text = text.value.trim(),
        gmText = gmText.value.trim(),
        isPinned = isPinned,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    override fun isValid() =
        name.isValid() &&
            name.value.length <= Skill.NAME_MAX_LENGTH &&
            text.value.length <= Skill.DESCRIPTION_MAX_LENGTH

    companion object {
        @Composable
        fun fromEntry(item: JournalEntry?) = JournalEntryFormData(
            id = remember { item?.id ?: uuid4() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            parents = inputValue(
                (item?.parents ?: emptyList())
                    .joinToString(" ${JournalEntry.PARENT_SEPARATOR} ")
            ),
            text = inputValue(item?.text ?: ""),
            gmText = inputValue(item?.gmText ?: ""),
            isPinned = item?.isPinned ?: false,
            isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
        )
    }
}
