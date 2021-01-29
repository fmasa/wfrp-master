package cz.frantisekmasa.wfrp_master.core.common

fun String.requireMaxLength(maxLength: Int, valueName: String)
    = require(length <= maxLength) {
    "Maximum allowed length of \"$valueName\" is $maxLength, \"$this\" given"
}