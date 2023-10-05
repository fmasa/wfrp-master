package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsCard
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitsCard
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ColumnSize
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Container
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SkillsScreen(
    characterId: CharacterId,
    state: SkillsScreenState,
    modifier: Modifier = Modifier,
    removeSkill: (Skill) -> Unit,
    removeTalent: (Talent) -> Unit,
    removeTrait: (Trait) -> Unit,
) {
    Container(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {
        val size = if (breakpoint > Breakpoint.XSmall) ColumnSize.HalfWidth else ColumnSize.FullWidth

        column(size) {
            SkillsCard(
                characterId = characterId,
                skills = state.skills,
                onRemove = removeSkill,
                characteristics = state.characteristics,
            )
        }

        column(size) {
            TalentsCard(
                characterId = characterId,
                talents = state.talents,
                onRemove = removeTalent,
            )

            TraitsCard(
                characterId = characterId,
                traits = state.traits,
                onRemove = removeTrait,
            )
        }
    }
}

data class SkillsScreenState(
    val characteristics: Stats,
    val skills: ImmutableList<Skill>,
    val talents: ImmutableList<Talent>,
    val traits: ImmutableList<Trait>,
)
