package cz.muni.fi.rpg.model

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.util.UUID

fun generateAccessCode(): String {
    // TODO: Replace with better strategy
    return UUID.randomUUID().toString()
}

internal fun <L, R> Flow<Either<L, R>>.right(): Flow<R> = transform {
    if (it is Either.Right) {
        emit(it.b)
    }
}