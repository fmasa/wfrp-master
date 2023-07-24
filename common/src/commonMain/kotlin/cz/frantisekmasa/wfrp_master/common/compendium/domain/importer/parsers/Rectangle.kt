package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

data class Rectangle(
    val color: Color,
    val points: List<Pair<Double, Double>>,
) {
    enum class Color { BRONZE, GOLD, SILVER }
}
