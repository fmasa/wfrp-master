package cz.frantisekmasa.wfrp_master.common.core.utils

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun <L, R> Flow<Either<L, R>>.right(): Flow<R> = transform {
    if (it is Either.Right) {
        emit(it.value)
    }
}
