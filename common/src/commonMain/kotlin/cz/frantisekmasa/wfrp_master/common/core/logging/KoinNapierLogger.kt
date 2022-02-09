package cz.frantisekmasa.wfrp_master.common.core.logging

import io.github.aakira.napier.Napier
import org.koin.core.logger.Level
import org.koin.core.logger.Logger

class KoinNapierLogger(level: Level = Level.INFO) : Logger(level) {

    override fun log(level: Level, msg: String) {
        when (level) {
            Level.DEBUG -> Napier.d(msg)
            Level.INFO -> Napier.i(msg)
            Level.ERROR -> Napier.e(msg)
            else -> Napier.e(msg)
        }
    }
}
