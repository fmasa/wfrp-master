package cz.frantisekmasa.wfrp_master.common.core.common

fun String.requireMaxLength(
    maxLength: Int,
    valueName: String,
) = require(length <= maxLength) {
    "Maximum allowed length of \"$valueName\" is $maxLength characters, \"$this\" given"
}
