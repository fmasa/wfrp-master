package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun TalentsCard(
    viewModel: TalentsViewModel,
    onRemove: (Talent) -> Unit,
) {
    val talents = viewModel.talents.observeAsState().value ?: return
    val coroutineScope = rememberCoroutineScope()
    val fragmentManager = fragmentManager()

    CardContainer(Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp)) {
        Column(Modifier.padding(horizontal = 6.dp)) {
            CardTitle(R.string.title_character_talents)

            if (talents.isNotEmpty()) {
                Column {
                    for (talent in talents) {
                        TalentItem(
                            talent,
                            onClick = {
                                with(coroutineScope) {
                                    openTalentDialog(
                                        talent,
                                        viewModel,
                                        fragmentManager,
                                    )
                                }
                            },
                            onRemove = { onRemove(talent) }
                        )
                    }
                }
            }

            CardButton(
                R.string.title_talent_add,
                onClick = {
                    with(coroutineScope) { openTalentDialog(null, viewModel, fragmentManager) }
                }
            )
        }
    }
}

@Composable
private fun TalentItem(talent: Talent, onClick: () -> Unit, onRemove: () -> Unit) {
    CardItem(
        name = talent.name,
        description = talent.description,
        iconRes = R.drawable.ic_skills,
        onClick = onClick,
        contextMenuItems = listOf(
            ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove() })
        ),
        badgeContent = { Text("+ ${talent.taken}") }
    )
}

private fun CoroutineScope.openTalentDialog(
    existingTalent: Talent?,
    viewModel: TalentsViewModel,
    fragmentManager: FragmentManager
) {
    val dialog = TalentDialog.newInstance(existingTalent)
    dialog.setOnSuccessListener { talent ->
        launch(Dispatchers.IO) {
            viewModel.saveTalent(talent)

            withContext(Dispatchers.Main) { dialog.dismiss() }
        }
    }

    dialog.show(fragmentManager, "TalentDialog")
}