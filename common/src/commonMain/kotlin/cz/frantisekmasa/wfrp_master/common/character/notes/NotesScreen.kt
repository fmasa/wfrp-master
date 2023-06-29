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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.ambitions.AmbitionsCard
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ColumnSize
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Container
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun NotesScreen(
    screenModel: CharacterScreenModel,
    character: Character,
    party: Party,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colors.background),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.small)
        ) {
            NoteCard(character, screenModel)
            MotivationCard(character, screenModel)

            if (character.type == CharacterType.PLAYER_CHARACTER) {
                AmbitionsContainer(character, party, screenModel)
            }
        }
    }
}

@Composable
private fun AmbitionsContainer(character: Character, party: Party, screenModel: CharacterScreenModel) {
    val strings = LocalStrings.current.ambition

    Container(
        horizontalArrangement = Arrangement.spacedBy(Spacing.gutterSize()),
    ) {
        val size = if (breakpoint > Breakpoint.XSmall)
            ColumnSize.HalfWidth
        else ColumnSize.FullWidth

        column(size) {
            AmbitionsCard(
                title = strings.titleCharacterAmbitions,
                ambitions = character.ambitions,
                onSave = { ambitions ->
                    screenModel.update { it.updateAmbitions(ambitions) }
                },
            )
        }

        column(size) {
            AmbitionsCard(
                title = strings.titlePartyAmbitions,
                ambitions = party.ambitions,
                titleIcon = Icons.Rounded.Group,
                onSave = null,
            )
        }
    }
}
