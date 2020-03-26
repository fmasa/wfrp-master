package cz.muni.fi.rpg.model

import com.google.android.gms.tasks.Task

interface PartyRepository {
    fun save(party: Party): Task<Void>
}