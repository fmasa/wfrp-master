package cz.muni.fi.rpg.common

typealias EntityListener<TEntity> = (item: TEntity) -> Unit
typealias SuspendableEntityListener<TEntity> = suspend (item: TEntity) -> Unit