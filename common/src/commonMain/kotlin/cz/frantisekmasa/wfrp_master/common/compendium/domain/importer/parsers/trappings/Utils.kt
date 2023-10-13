package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

inline fun <reified T : Enum<T>> matchEnumOrNull(
    value: String,
    aliases: Map<String, T> = emptyMap(),
): T? {
    if (value in aliases) {
        return aliases.getValue(value)
    }

    val comparableValue = value
        .replace('-', '_')
        .replace(' ', '_')

    return enumValues<T>().firstOrNull { feature ->
        feature.name.equals(comparableValue, ignoreCase = true)
    }
}

inline fun <reified T : Enum<T>> matchEnumSetOrNull(value: String, separator: String): Set<T>? {
    val items = value.split(separator, ignoreCase = true)
    val enums = items.mapNotNull { matchEnumOrNull<T>(it.trim()) }

    if (enums.size < items.size) {
        return null
    }

    return enums.toSet()
}

val FEATURE_REGEX = Regex("([a-zA-Z- ]+) ?\\(?\\+?([0-9])?A?\\)?")
private val NAME_WITH_COUNT_PATTERN = Regex("(.*) \\((\\d+|dozen)\\)")

inline fun <reified T : Enum<T>> parseFeatures(value: String): Map<T, Int> {
    if (value == "–" || value == "") {
        return emptyMap()
    }

    return value.replace("*", "")
        .splitToSequence(",")
        .map { it.trim() }
        .mapNotNull {
            val (_, name, rating) = FEATURE_REGEX.matchEntire(it)?.groupValues
                ?: error("Invalid feature $it")
            val feature = matchEnumOrNull<T>(name.trim()) ?: return@mapNotNull null

            feature to (rating.toIntOrNull() ?: 1)
        }.toMap()
}

fun parseNameAndPackSize(value: String): Pair<String, Int> {
    val result = NAME_WITH_COUNT_PATTERN.matchEntire(value)
        ?: return value to 1

    return Pair(
        result.groupValues[1],
        if (result.groupValues[2] == "dozen")
            12
        else result.groupValues[2].toInt(),
    )
}
