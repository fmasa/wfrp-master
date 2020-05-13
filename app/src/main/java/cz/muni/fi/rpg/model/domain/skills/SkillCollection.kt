package cz.muni.fi.rpg.model.domain.skills

import androidx.lifecycle.LiveData
import java.util.UUID

interface SkillCollection {
    suspend fun set(id: UUID, skill: Skill)

    suspend fun remove(id: UUID)

    suspend fun get(id: UUID) : Skill?

    fun live(): LiveData<List<Skill>>
}