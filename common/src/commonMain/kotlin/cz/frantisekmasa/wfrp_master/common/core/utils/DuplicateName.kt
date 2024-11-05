package cz.frantisekmasa.wfrp_master.common.core.utils

const val SUFFIX = " (Copy)"

fun duplicateName(
    name: String,
    maxLength: Int,
): String =
    buildString {
        if (name.length + SUFFIX.length <= maxLength) {
            append(name)
        } else {
            append(name.substring(0, maxLength - SUFFIX.length - 1))
            append('…')
        }

        append(SUFFIX)
    }
