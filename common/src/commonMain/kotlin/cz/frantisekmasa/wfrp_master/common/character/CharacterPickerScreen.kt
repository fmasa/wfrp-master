package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.characterCreation.CharacterCreationScreen
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
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

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(characters) {
            when {
                characters.isEmpty() -> navigator.replace(
                    CharacterCreationScreen(partyId, CharacterType.PLAYER_CHARACTER, userId)
                )
                characters.size == 1 -> navigator.replace(
                    CharacterDetailScreen(
                        CharacterId(partyId, characters.first().id)
                    )
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(LocalStrings.current.character.titleSelectCharacter) }
                )
            }
        ) {
            Card {
                LazyColumn {
                    items(characters, key = { it.id }) { character ->
                        CardItem(
                            name = character.name,
                            icon = { CharacterAvatar(character.avatarUrl, ItemIcon.Size.Small) },
                            onClick = {
                                navigator.replace(
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
    }
}
