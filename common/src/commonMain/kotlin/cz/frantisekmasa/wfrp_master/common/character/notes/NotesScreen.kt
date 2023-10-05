package cz.frantisekmasa.wfrp_master.common.character.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.ambitions.AmbitionsCard
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ColumnSize
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Container
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NotesScreen(
    state: NotesScreenState,
    modifier: Modifier = Modifier,
    updateNote: suspend (String) -> Unit,
    updateMotivation: suspend (String) -> Unit,
    updateCharacterAmbitions: suspend (Ambitions) -> Unit,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colors.background),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.small)
        ) {
            NoteCard(
                title = { stringResource(Str.character_note) },
                text = state.characterNote,
                updateDialogTitle = { stringResource(Str.character_title_edit_note) },
                onUpdate = updateNote,
                maxLength = Character.NOTE_MAX_LENGTH,
            )

            NoteCard(
                title = { stringResource(Str.character_motivation) },
                text = state.characterMotivation,
                updateDialogTitle = { stringResource(Str.character_title_edit_motivation) },
                onUpdate = updateMotivation,
                maxLength = Character.MOTIVATION_MAX_LENGTH,
            )

            if (state.characterType == CharacterType.PLAYER_CHARACTER) {
                AmbitionsContainer(
                    characterAmbitions = state.characterAmbitions,
                    partyAmbitions = state.partyAmbitions,
                    updateCharacterAmbitions = updateCharacterAmbitions,
                )
            }
        }
    }
}

@Composable
private fun AmbitionsContainer(
    characterAmbitions: Ambitions,
    partyAmbitions: Ambitions,
    updateCharacterAmbitions: suspend (Ambitions) -> Unit,
) {
    Container(
        horizontalArrangement = Arrangement.spacedBy(Spacing.gutterSize()),
    ) {
        val size = if (breakpoint > Breakpoint.XSmall)
            ColumnSize.HalfWidth
        else ColumnSize.FullWidth

        column(size) {
            AmbitionsCard(
                title = stringResource(Str.ambition_title_character_ambitions),
                ambitions = characterAmbitions,
                onSave = updateCharacterAmbitions,
            )
        }

        column(size) {
            AmbitionsCard(
                title = stringResource(Str.ambition_title_party_ambitions),
                ambitions = partyAmbitions,
                titleIcon = Icons.Rounded.Group,
                onSave = null,
            )
        }
    }
}
