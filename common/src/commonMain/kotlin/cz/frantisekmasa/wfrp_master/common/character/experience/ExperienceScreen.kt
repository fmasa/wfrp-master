package cz.frantisekmasa.wfrp_master.common.character.experience

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import dev.icerock.moko.resources.compose.stringResource

data class ExperienceScreen(
    private val characterId: CharacterId,
) : Screen {
    override val key = "parties/${characterId.partyId}/${characterId.id}/experience"

    @Composable
    override fun Content() {
        Scaffold(
           topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.character_title_experience)) }
                )
           },
        ) {
           Column {
               Row {
               }
           }
        }

    }
}