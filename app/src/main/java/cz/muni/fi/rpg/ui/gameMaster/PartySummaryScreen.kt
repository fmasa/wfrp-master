package cz.muni.fi.rpg.ui.gameMaster

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumViewModel
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.AmbitionsCard
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import cz.muni.fi.rpg.ui.gameMaster.rolls.SkillTestDialog
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
internal fun PartySummaryScreen(
    partyId: PartyId,
    routing: Routing<*>,
    modifier: Modifier,
    viewModel: GameMasterViewModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: String?) -> Unit,
) {
    var skillTestDialogVisible by rememberSaveable { mutableStateOf(false) }

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
                Icon(
                    painterResource(R.drawable.ic_dice_roll),
                    stringResource(R.string.icon_hidden_skill_test),
                )
            }
        }
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(bottom = Spacing.bottomPaddingUnderFab)
        ) {
            val party = viewModel.party.observeAsState().value
                ?: return@Column

            var invitationDialogVisible by remember { mutableStateOf(false) }

            if (invitationDialogVisible) {
                InvitationDialog2(
                    invitation = party.getInvitation(),
                    onDismissRequest = { invitationDialogVisible = false },
                )
            }

            val coroutineScope = rememberCoroutineScope()

            PlayersCard(
                viewModel,
                onCharacterOpenRequest = onCharacterOpenRequest,
                onCharacterCreateRequest = onCharacterCreateRequest,
                onRemoveCharacter = {
                    // TODO: Remove this character from combat (see [Combat::removeNpc()])
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.archiveCharacter(CharacterId(partyId, it.id))
                    }
                },
                onInvitationDialogRequest = { invitationDialogVisible = true },
            )

            AmbitionsCard(
                modifier = Modifier.padding(horizontal = 8.dp),
                titleRes = R.string.title_party_ambitions,
                ambitions = party.getAmbitions(),
                onSave = { viewModel.updatePartyAmbitions(it) },
            )

            CardContainer(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = { routing.navigateTo(Route.Compendium(partyId)) })
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
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
    itemsCount: LiveData<List<T>>,
) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            itemsCount.observeAsState().value?.size?.toString() ?: "?",
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
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {

            CardTitle(R.string.title_characters)

            val players = viewModel.players.observeAsState().value

            when {
                players == null -> {
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.CenterHorizontally)
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

                val party = viewModel.party.observeAsState().value

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
                    listOf(
                        ContextMenu.Item(stringResource(R.string.remove)) {
                            onRemoveCharacter(character)
                        }
                    )
                else emptyList()
            )
        }
    }
}
