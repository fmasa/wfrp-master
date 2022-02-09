package cz.frantisekmasa.wfrp_master.common.core.utils

fun <T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> {
    if (sourceIndex == targetIndex) {
        return this
    }

    val item = get(sourceIndex)
    val otherItems = filterIndexed { index, _ -> index != sourceIndex }

    return otherItems.take(targetIndex) + item + otherItems.drop(targetIndex)
}
