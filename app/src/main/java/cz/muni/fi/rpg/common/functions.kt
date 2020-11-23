package cz.muni.fi.rpg.common

typealias EntityListener<TEntity> = (item: TEntity) -> Unit
typealias SuspendableEntityListener<TEntity> = suspend (item: TEntity) -> Unit

fun String.requireMaxLength(maxLength: Int, valueName: String)
    = require(length <= maxLength) {
    "Maximum allowed length of \"$valueName\" is $maxLength, \"$this\" given"
}