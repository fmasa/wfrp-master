package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.talents.talentsCard
import cz.frantisekmasa.wfrp_master.common.character.traits.traitsCard
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalBreakpoint
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
    if (LocalBreakpoint.current > Breakpoint.XSmall) {
        Row(
            modifier
                .padding(Spacing.small)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            CardContainer(Modifier.weight(1f)) {
                LazyColumn(Modifier.fillMaxWidth()) {
                    skillsCard(
                        characterId = characterId,
                        skills = state.skills,
                        onRemove = removeSkill,
                        characteristics = state.characteristics,
                    )
                }
            }

            CardContainer(Modifier.weight(1f)) {

                LazyColumn(Modifier.fillMaxWidth()) {
                    talentsCard(
                        characterId = characterId,
                        talents = state.talents,
                        onRemove = removeTalent,
                    )

                    item {
                        SectionSeparator()
                    }

                    traitsCard(
                        characterId = characterId,
                        traits = state.traits,
                        onRemove = removeTrait,
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            skillsCard(
                characterId = characterId,
                skills = state.skills,
                onRemove = removeSkill,
                characteristics = state.characteristics,
            )

            item {
                SectionSeparator()
            }

            talentsCard(
                characterId = characterId,
                talents = state.talents,
                onRemove = removeTalent,
            )

            item {
                SectionSeparator()
            }

            traitsCard(
                characterId = characterId,
                traits = state.traits,
                onRemove = removeTrait,
            )
        }
    }
}

@Composable
private fun SectionSeparator() {
    Column {
        Spacer(Modifier.height(Spacing.medium))
        Divider()
    }
}

data class SkillsScreenState(
    val characteristics: Stats,
    val skills: ImmutableList<Skill>,
    val talents: ImmutableList<Talent>,
    val traits: ImmutableList<Trait>,
)
