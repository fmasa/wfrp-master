package cz.muni.fi.rpg.common.log

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import timber.log.Timber

class KoinTimberLogger(level: Level = Level.INFO) : Logger(level) {

    private val tree = Timber.tag(KOIN_TAG)

    override fun log(level: Level, msg: String) {
        when (level) {
            Level.DEBUG -> tree.d(msg)
            Level.INFO -> tree.i(msg)
            Level.ERROR -> tree.e(msg)
            else -> tree.e(msg)
        }
    }
}