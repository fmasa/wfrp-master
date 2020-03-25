package cz.muni.fi.rpg.model

import com.google.common.collect.ImmutableList

data class Party(
    val id: String? = null,
    val name: String? = null,
    val gameMasterId: String? = null,
    val users: List<String>? = null,
    val accessCode: String? = null
) {
}