package cz.frantisekmasa.wfrp_master.common.gameMaster.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumListScreen
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.time.DateTime
import cz.frantisekmasa.wfrp_master.common.core.domain.time.ImperialDate
import cz.frantisekmasa.wfrp_master.common.core.domain.time.MannsliebPhase
import cz.frantisekmasa.wfrp_master.common.core.domain.time.YearSeason
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.core.ui.timePicker
import cz.frantisekmasa.wfrp_master.common.encounters.EncountersScreen
import cz.frantisekmasa.wfrp_master.common.gameMaster.GameMasterScreenModel
import cz.frantisekmasa.wfrp_master.common.npcs.NpcsScreen
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
internal fun WorldScreen(
    party: Party,
    screenModel: GameMasterScreenModel,
    modifier: Modifier,
) {
    Column(
        modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 6.dp)
            .padding(horizontal = Spacing.small)
    ) {
        CalendarCard(screenModel, party.time)
        NavigationCard(party.id)
    }
}

@Composable
private fun CalendarCard(screenModel: GameMasterScreenModel, dateTime: DateTime) {
    CardContainer {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Time(
                screenModel = screenModel,
                time = dateTime.time,
            )
            Date(
                screenModel = screenModel,
                date = dateTime.date,
            )
        }
    }
}

@Composable
private fun NavigationCard(partyId: PartyId) {
    CardContainer {
        val navigation = LocalNavigationTransaction.current

        ListItem(
            modifier = Modifier.clickable { navigation.navigate(NpcsScreen(partyId)) },
            icon = { ItemIcon(Resources.Drawable.Npc) },
            text = { Text(stringResource(Str.npcs_title_plural)) },
        )

        ListItem(
            modifier = Modifier.clickable { navigation.navigate(EncountersScreen(partyId)) },
            icon = { ItemIcon(Resources.Drawable.Encounter) },
            text = { Text(stringResource(Str.encounters_title)) },
        )

        ListItem(
            modifier = Modifier.clickable { navigation.navigate(CompendiumListScreen(partyId)) },
            icon = { ItemIcon(Resources.Drawable.Compendium) },
            text = { Text(stringResource(Str.compendium_title)) },
        )

        ListItem(
            modifier = Modifier.clickable { navigation.navigate(JournalScreen(partyId)) },
            icon = { ItemIcon(Resources.Drawable.JournalEntry) },
            text = { Text(stringResource(Str.compendium_title_journal)) },
        )
    }
}

@Composable
private fun Time(screenModel: GameMasterScreenModel, time: DateTime.TimeOfDay) {
    val coroutineScope = rememberCoroutineScope()

    val dialog = timePicker(
        remember(time) { LocalTime.of(time.hour, time.minute) },
        onTimeChange = { newTime ->
            coroutineScope.launch(Dispatchers.IO) {
                screenModel.changeTime {
                    it.withTime(DateTime.TimeOfDay(newTime.getHour(), newTime.getMinute()))
                }

                hide()
            }
        }
    )

    Text(
        time.format(),
        style = MaterialTheme.typography.h5,
        modifier = Modifier.clickable(onClick = { dialog.show() }),
    )
}

@Composable
private fun Date(screenModel: GameMasterScreenModel, date: ImperialDate) {
    var dialogVisible by rememberSaveable { mutableStateOf(false) }

    if (dialogVisible) {
        FullScreenDialog(onDismissRequest = { dialogVisible = false }) {
            var selectedDate by rememberSaveable { mutableStateOf(date) }

            val coroutineScope = rememberCoroutineScope()
            ImperialCalendar(
                date = selectedDate,
                onDateChange = { selectedDate = it },
                actions = {
                    SaveAction(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.changeTime { it.copy(date = selectedDate) }
                                dialogVisible = false
                            }
                        }
                    )
                }
            )
        }
    }

    Text(
        date.format(),
        modifier = Modifier.clickable(onClick = { dialogVisible = true }),
        style = MaterialTheme.typography.h6
    )
    Text(YearSeason.at(date).readableName, modifier = Modifier.padding(top = 8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Rounded.NightsStay,
            VisualOnlyIconDescription,
            Modifier.padding(end = 4.dp),
        )
        Text(
            buildAnnotatedString {
                append(stringResource(Str.calendar_mannslieb_phase))
                append(": ")
                append(MannsliebPhase.at(date).localizedName)
            }
        )
    }
}
