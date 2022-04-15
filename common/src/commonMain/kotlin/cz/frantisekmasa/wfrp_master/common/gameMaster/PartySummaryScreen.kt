package cz.frantisekmasa.wfrp_master.common.gameMaster

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.ambitions.AmbitionsCard
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreen
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationDialog
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.skillTest.SkillTestDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Composable
internal fun Screen.PartySummaryScreen(
    partyId: PartyId,
    modifier: Modifier,
    screenModel: GameMasterScreenModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: UserId?) -> Unit,
) {
    var skillTestDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (skillTestDialogVisible) {
        SkillTestDialog(
            screenModel = rememberScreenModel(arg = partyId),
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
            val party = screenModel.party.collectWithLifecycle(null).value
                ?: return@Column

            var invitationDialogVisible by remember { mutableStateOf(false) }

            if (invitationDialogVisible) {
                InvitationDialog(
                    invitation = party.getInvitation(),
                    screenModel = rememberScreenModel(),
                    onDismissRequest = { invitationDialogVisible = false },
                )
            }

            val coroutineScope = rememberCoroutineScope()

            PlayersCard(
                screenModel,
                onCharacterOpenRequest = onCharacterOpenRequest,
                onCharacterCreateRequest = onCharacterCreateRequest,
                onRemoveCharacter = {
                    // TODO: Remove this character from combat (see [Combat::removeNpc()])
                    coroutineScope.launch(Dispatchers.IO) {
                        screenModel.archiveCharacter(CharacterId(partyId, it.id))
                    }
                },
                onInvitationDialogRequest = { invitationDialogVisible = true },
            )

            AmbitionsCard(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = strings.ambition.titlePartyAmbitions,
                ambitions = party.ambitions,
                onSave = { screenModel.updatePartyAmbitions(it) },
            )

            val navigator = LocalNavigator.currentOrThrow

            CardContainer(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable(onClick = { navigator.push(CompendiumScreen(partyId)) })
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    val strings = LocalStrings.current.compendium
                    CardTitle(strings.title)

                    val compendium: CompendiumScreenModel = rememberScreenModel(arg = partyId)

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
    screenModel: GameMasterScreenModel,
    onCharacterOpenRequest: (Character) -> Unit,
    onCharacterCreateRequest: (userId: UserId?) -> Unit,
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

            val players = screenModel.players.collectWithLifecycle(null).value

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

                val party = screenModel.party.collectWithLifecycle(null).value

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
    onCharacterCreateRequest: (userId: UserId) -> Unit,
    onRemoveCharacter: (Character) -> Unit
) {
    val strings = LocalStrings.current

    when (player) {
        is Player.UserWithoutCharacter -> {
            ProvideTextStyle(TextStyle.Default.copy(fontStyle = FontStyle.Italic)) {
                CardItem(
                    name = strings.parties.messages.waitingForPlayerCharacter,
                    icon = { CharacterAvatar(null, ItemIcon.Size.Small) },
                    onClick = { onCharacterCreateRequest(UserId(player.userId)) },
                    contextMenuItems = emptyList(),
                )
            }
        }
        is Player.ExistingCharacter -> {
            val character = player.character

            CardItem(
                name = character.name,
                icon = { CharacterAvatar(character.avatarUrl, ItemIcon.Size.Small) },
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
