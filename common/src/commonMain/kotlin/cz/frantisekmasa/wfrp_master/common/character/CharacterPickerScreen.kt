package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

data class CharacterPickerScreen(
    private val partyId: PartyId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: CharacterPickerScreenModel = rememberScreenModel(arg = partyId)
        val userId = LocalUser.current.id

        val characters = remember { screenModel.allUserCharacters(userId) }
            .collectWithLifecycle(null).value

        if (characters == null) {
            FullScreenProgress()
            return
        }

        val navigation = LocalNavigationTransaction.current

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(LocalStrings.current.character.titleSelectCharacter) }
                )
            }
        ) {
            when {
                characters.size == 1 -> {
                    LaunchedEffect(Unit) {
                        navigation.replace(
                            CharacterDetailScreen(
                                CharacterId(partyId, characters.first().id)
                            )
                        )
                    }
                }

                characters.isNotEmpty() -> {
                    CharacterPicker(characters)
                }

                else -> {
                    NoCharacters(screenModel)
                }
            }
        }
    }

    @Composable
    private fun CharacterPicker(characters: List<Character>) {
        Card {
            val navigation = LocalNavigationTransaction.current

            LazyColumn {
                items(characters, key = { it.id }) { character ->
                    CardItem(
                        name = character.name,
                        icon = { CharacterAvatar(character.avatarUrl, ItemIcon.Size.Small) },
                        onClick = {
                            navigation.replace(
                                CharacterDetailScreen(
                                    CharacterId(partyId, character.id)
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun NoCharacters(screenModel: CharacterPickerScreenModel) {
        val unassignedCharacters =
            screenModel.unassignedPlayerCharacters.collectWithLifecycle(null).value

        if (unassignedCharacters == null) {
            FullScreenProgress()
            return
        }

        val navigation = LocalNavigationTransaction.current
        var unassignedCharactersDialogVisible by remember { mutableStateOf(false) }

        if (unassignedCharactersDialogVisible) {
            UnassignedCharacterPickerDialog(
                partyId = partyId,
                unassignedCharacters = unassignedCharacters,
                screenModel = screenModel,
                onDismissRequest = { unassignedCharactersDialogVisible = false },
                onAssigned = {
                    navigation.replace(CharacterDetailScreen(it))
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.bodyPadding)
        ) {
            EmptyUI(
                text = LocalStrings.current.character.messages.noCharacterInParty,
                icon = Resources.Drawable.Character,
                subText = if (unassignedCharacters.isNotEmpty())
                    LocalStrings.current.character.messages.unassignedCharactersExist
                else null,
            )

            if (unassignedCharacters.isNotEmpty()) {
                OutlinedButton(onClick = { unassignedCharactersDialogVisible = true }) {
                    Text(LocalStrings.current.character.buttonChoose.uppercase())
                }

                val userId = LocalUser.current.id
                Button(
                    onClick = {
                        navigation.replace(
                            CharacterCreationScreen(
                                partyId,
                                CharacterType.PLAYER_CHARACTER,
                                userId = userId,
                            )
                        )
                    }
                ) {
                    Text(LocalStrings.current.character.buttonAdd.uppercase())
                }
            }
        }
    }
}
