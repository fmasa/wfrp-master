package cz.muni.fi.rpg.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import arrow.core.Either
import java.util.*


fun generateAccessCode(): String {
    // TODO: Replace with better strategy
    return UUID.randomUUID().toString()
}

fun <L, R> LiveData<Either<L, R>>.right(): LiveData<R> {
    val mediator = MediatorLiveData<R>()

    mediator.addSource(this) { it.map(mediator::setValue) }

    return mediator
}