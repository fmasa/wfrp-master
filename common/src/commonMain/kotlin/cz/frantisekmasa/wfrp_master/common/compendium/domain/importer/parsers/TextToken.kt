package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

data class TextToken(
    val text: String,
    val fontName: String,
    val height: Float,
    val fontSizePt: Float,
    val y: Float,
) {
    val metadata get() = Token.Metadata(
        y = y,
        height = height,
        )
}
