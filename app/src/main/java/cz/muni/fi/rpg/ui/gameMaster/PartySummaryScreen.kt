package cz.muni.fi.rpg.ui.gameMaster

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.github.zsoltk.compose.router.BackStack
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumViewModel
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.muni.fi.rpg.ui.gameMaster.rolls.SkillTestDialog
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
internal fun PartySummaryScreen(
    partyId: UUID,
    backStack: BackStack<Route>,
    modifier: Modifier,
    viewModel: GameMasterViewModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: String?) -> Unit,
    onEditAmbitionsRequest: (Ambitions) -> Unit,
) {
    var skillTestDialogVisible by savedInstanceState { false }

    if (skillTestDialogVisible) {
        SkillTestDialog(
            partyId = partyId,
            onDismissRequest = { skillTestDialogVisible = false }
        )
    }

    Scaffold(
        modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { skillTestDialogVisible = true }) {
                loadVectorResource(R.drawable.ic_dice_roll).resource.resource?.let { Icon(it) }
            }
        }
    ) {
        ScrollableColumn(Modifier.background(MaterialTheme.colors.background)) {
            val party = viewModel.party.collectAsState(null).value
                ?: return@ScrollableColumn

            val invitationDialogVisible = remember { mutableStateOf(false) }

            if (invitationDialogVisible.value) {
                InvitationDialog2(
                    invitation = party.getInvitation(),
                    onDismissRequest = { invitationDialogVisible.value = false },
                )
            }

            val coroutineScope = rememberCoroutineScope()

            PlayersCard(
                viewModel,
                onCharacterOpenRequest = onCharacterOpenRequest,
                onCharacterCreateRequest = onCharacterCreateRequest,
                onRemoveCharacter = {
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.archiveCharacter(CharacterId(partyId, it.id))
                    }
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


            CardContainer(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = { backStack.push(Route.Compendium(partyId)) })
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    CardTitle(R.string.title_compendium)

                    val compendium: CompendiumViewModel by viewModel { parametersOf(partyId) }

                    Row {
                        CompendiumSummary(R.string.title_character_skills, compendium.skills)
                        CompendiumSummary(R.string.title_character_talents, compendium.talents)
                        CompendiumSummary(R.string.title_character_spells, compendium.spells)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> RowScope.CompendiumSummary(
    @StringRes text: Int,
    itemsCount: Flow<List<T>>,
) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            itemsCount.collectAsState(null).value?.size?.toString() ?: "?",
            style = MaterialTheme.typography.h6
        )
        Text(stringResource(text))
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

            val players = viewModel.getPlayers().collectAsState(null).value

            when {
                players == null -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.CenterHorizontally)
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

                val party = viewModel.party.collectAsState(null).value

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