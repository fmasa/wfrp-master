package cz.muni.fi.rpg.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
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


fun <T> LiveData<T?>.notNull(): LiveData<T> {
    val mediator = MediatorLiveData<T>()

    mediator.addSource(this) {
        if (it != null) {
            mediator.value = it
        }
    }

    return mediator
}

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y) = Transformations.map(this, mapFunction)
fun <T> LiveData<T>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)
fun <X, Y> LiveData<X>.switchMap(mapFunction: (X) -> LiveData<Y>) = Transformations.switchMap(this, mapFunction)