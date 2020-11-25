package cz.muni.fi.rpg.ui.character.talents.dialog

import android.widget.Toast
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.exceptions.CompendiumItemNotFound
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@Composable
internal fun TimesTakenForm(
    existingTalent: Talent?,
    compendiumTalentId: UUID,
    viewModel: TalentsViewModel,
    onComplete: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var timesTaken by savedInstanceState { existingTalent?.taken ?: 1 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingTalent != null)
                                R.string.title_talent_edit else
                                R.string.title_talent_new
                        )
                    )
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()
                    val context = ContextAmbient.current

                    TopBarAction(
                        enabled = !saving,
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true

                                try {
                                    viewModel.saveCompendiumTalent(
                                        talentId = existingTalent?.id ?: UUID.randomUUID(),
                                        compendiumTalentId = compendiumTalentId,
                                        timesTaken = timesTaken,
                                    )
                                } catch(e: CompendiumItemNotFound) {
                                    Timber.d(e)

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_compendium_talent_removed),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                } finally {
                                    withContext(Dispatchers.Main) { onComplete() }
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
                    }

                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        ScrollableColumn(contentPadding = PaddingValues(BodyPadding)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.label_talent_taken),
                    modifier = Modifier.weight(1f),
                )
                NumberPicker(
                    value = timesTaken,
                    onIncrement = { timesTaken++ },
                    onDecrement = {
                        if (timesTaken > 1) {
                            timesTaken--
                        }
                    }
                )
            }
        }
    }
}