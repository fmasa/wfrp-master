package cz.muni.fi.rpg.common

import android.view.ViewGroup

typealias EntityListener<TEntity> = (item: TEntity) -> Unit
typealias SuspendableEntityListener<TEntity> = suspend (item: TEntity) -> Unit