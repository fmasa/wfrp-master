package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

private fun <T> State<T>.getValue() = this.value

@ExperimentalCoroutinesApi
@Composable
fun PartySummaryScreen(
    partyId: UUID,
    modifier: Modifier,
    viewModel: GameMasterViewModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: String?) -> Unit,
    onEditAmbitionsRequest: (Ambitions) -> Unit,
) {
    ScrollableColumn(modifier.background(MaterialTheme.colors.background)) {
        val party = viewModel.party.right().observeAsState().value
            ?: return@ScrollableColumn

        val invitationDialogVisible = remember { mutableStateOf(false) }

        if (invitationDialogVisible.value) {
            InvitationDialog2(
                invitation = party.getInvitation(),
                onDismissRequest = { invitationDialogVisible.value = false },
            )
        }

        PlayersCard(
            viewModel,
            onCharacterOpenRequest = onCharacterOpenRequest,
            onCharacterCreateRequest = onCharacterCreateRequest,
            onRemoveCharacter = {
                viewModel.archiveCharacter(CharacterId(partyId, it.id))
            },
            onInvitationDialogRequest = { invitationDialogVisible.value = true },
        )


        AmbitionsCard(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable(onClick = { onEditAmbitionsRequest(party.getAmbitions()) }),
            titleRes = R.string.title_party_ambitions,
            ambitions = party.getAmbitions()
        )
    }
}

@Composable
private fun PlayersCard(
    viewModel: GameMasterViewModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: String?) -> Unit,
    onRemoveCharacter: (Character) -> Unit,
    onInvitationDialogRequest: (Invitation) -> Unit,
) {
    CardContainer(Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.fillMaxWidth()) {

            CardTitle(R.string.title_characters)

            val players = viewModel.getPlayers().observeAsState().value

            when {
                players == null -> {
                    CircularProgressIndicator(
                        Modifier.gravity(Alignment.CenterHorizontally)
                            .gravity(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                    )
                }
                players.isEmpty() -> {
                    EmptyUI(
                        textId = R.string.no_characters_in_party_prompt,
                        drawableResourceId = R.drawable.ic_group,
                        size = EmptyUI.Size.Small,
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        for (player in players) {
                            PlayerItem(
                                player = player,
                                onCharacterOpenRequest = onCharacterOpenRequest,
                                onCharacterCreateRequest = onCharacterCreateRequest,
                                onRemoveCharacter = onRemoveCharacter,
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PrimaryButton(R.string.button_create, onClick = { onCharacterCreateRequest(null) })

                val party = viewModel.party.right().observeAsState().value

                PrimaryButton(
                    R.string.button_invite,
                    enabled = party !== null,
                    onClick = { party?.let { onInvitationDialogRequest(party.getInvitation()) } },
                )
            }
        }
    }
}

@Composable
private fun PlayerItem(
    player: Player,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: String) -> Unit,
    onRemoveCharacter: (Character) -> Unit
) {
    val icon = R.drawable.ic_character

    when (player) {
        is Player.UserWithoutCharacter -> {
            ProvideTextStyle(TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
                CardItem(
                    name = stringResource(R.string.waiting_for_character),
                    iconRes = icon,
                    onClick = { onCharacterCreateRequest(player.userId) },
                    contextMenuItems = emptyList(),
                )
            }
        }
        is Player.ExistingCharacter -> {
            val character = player.character

            CardItem(
                name = character.getName(),
                iconRes = icon,
                onClick = { onCharacterOpenRequest(character) },
                contextMenuItems = if (character.userId == null)
                    listOf(ContextMenu.Item(stringResource(R.string.remove)) {
                        onRemoveCharacter(character)
                    })
                else emptyList()
            )
        }
    }
}