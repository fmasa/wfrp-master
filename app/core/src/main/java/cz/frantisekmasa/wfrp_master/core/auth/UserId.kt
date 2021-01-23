package cz.frantisekmasa.wfrp_master.core.auth

data class UserId internal constructor(private val value: String) {
    companion object {
        fun fromString(userId: String): UserId = UserId(userId)
    }

    override fun toString() = value
}