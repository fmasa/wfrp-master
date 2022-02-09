package cz.frantisekmasa.wfrp_master.common.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<List<T>>.mapItems(transformation: (T) -> R) =
    map { items -> items.map(transformation) }
