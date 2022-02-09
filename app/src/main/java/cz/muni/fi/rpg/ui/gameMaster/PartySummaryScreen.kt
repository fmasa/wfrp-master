package cz.muni.fi.rpg.ui.gameMaster

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.ui.CompendiumViewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.ui.common.composables.AmbitionsCard
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.muni.fi.rpg.ui.gameMaster.adapter.Player
import cz.muni.fi.rpg.ui.gameMaster.rolls.SkillTestDialog
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    val strings = LocalStrings.current

    Scaffold(
        modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { skillTestDialogVisible = true }) {
                Icon(
                    drawableResource(Resources.Drawable.DiceRoll),
                    strings.tests.buttonHiddenSkillTest,
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
            val party = viewModel.party.collectWithLifecycle(null).value
                ?: return@Column

            var invitationDialogVisible by remember { mutableStateOf(false) }

            if (invitationDialogVisible) {
                InvitationDialog(
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
                title = strings.ambition.titlePartyAmbitions,
                ambitions = party.ambitions,
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
                    val strings = LocalStrings.current.compendium
                    CardTitle(strings.title)

                    val compendium: CompendiumViewModel by viewModel { parametersOf(partyId) }

                    Row {
                        CompendiumSummary(strings.tabSkills, compendium.skills)
                        CompendiumSummary(strings.tabTalents, compendium.talents)
                        CompendiumSummary(strings.tabSpells, compendium.spells)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> RowScope.CompendiumSummary(
    text: String,
    itemsCount: Flow<List<T>>,
) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            itemsCount.collectWithLifecycle(null).value?.size?.toString() ?: "?",
            style = MaterialTheme.typography.h6
        )
        Text(text)
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
            val strings = LocalStrings.current.parties

            CardTitle(strings.titleCharacters)

            val players = viewModel.players.collectWithLifecycle(null).value

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
                        text = strings.messages.noCharactersInParty,
                        icon = Icons.Rounded.Group,
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
                PrimaryButton(
                    LocalStrings.current.commonUi.buttonCreate,
                    onClick = { onCharacterCreateRequest(null) },
                )

                val party = viewModel.party.collectWithLifecycle(null).value

                PrimaryButton(
                    LocalStrings.current.parties.buttonInvite,
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
    val strings = LocalStrings.current

    when (player) {
        is Player.UserWithoutCharacter -> {
            ProvideTextStyle(TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
                CardItem(
                    name = strings.parties.messages.waitingForPlayerCharacter,
                    icon = { CharacterAvatar(null, ItemIcon.Size.Small) },
                    onClick = { onCharacterCreateRequest(player.userId) },
                    contextMenuItems = emptyList(),
                )
            }
        }
        is Player.ExistingCharacter -> {
            val character = player.character

            CardItem(
                name = character.getName(),
                icon = { CharacterAvatar(character.getAvatarUrl(), ItemIcon.Size.Small) },
                onClick = { onCharacterOpenRequest(character) },
                contextMenuItems = if (character.userId == null)
                    listOf(
                        ContextMenu.Item(strings.commonUi.buttonRemove) {
                            onRemoveCharacter(character)
                        }
                    )
                else emptyList()
            )
        }
    }
}
