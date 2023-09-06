package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.careers

import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.NullWriter
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Page
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfTextStripper
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Rectangle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Rectangle.Color
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.RectangleFinder
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic

/**
 * Careers do not have easily-readable Characteristics listed for each Career level.
 * The characteristics are denoted in table using icons and colors.
 *
 * So this parser searches for known characteristic shortcuts in table header with their position.
 * The Cross icon used as symbol for 1st level exists as text with specific font, so it's
 * extracted using similar logic to other text-operations.
 *
 * For 2-4th levels the parser searches for colored rectangles.
 *
 * Then the parser checks under which Characteristic does the given Cross or rectangle occur.
 */
class CareerCharacteristicsParser(
    private val document: Document,
    private val structure: PdfStructure,
) {
    fun findCharacteristics(pageNumber: Int): List<UpgradableCharacteristic> {
        val stripper = TextStripper()
        stripper.setStartPage(pageNumber)
        stripper.setEndPage(pageNumber)
        stripper.writeText(document, NullWriter())

        val page = document.getPage(pageNumber - 1)
        val rectangleProcessor = RectangleProcessor(page)
        rectangleProcessor.processPage(page)

        return buildList {
            for (rectangle in rectangleProcessor.rectangles) {
                val characteristic = stripper.characteristicCells.firstOrNull { cell ->
                    rectangle.points.minOf { it.first } <= cell.xRange.start &&
                        rectangle.points.maxOf { it.second } >= cell.xRange.endInclusive
                } ?: continue

                add(
                    UpgradableCharacteristic(
                        characteristic = characteristic.characteristic,
                        level = when (rectangle.color) {
                            Color.BRONZE -> 2
                            Color.SILVER -> 3
                            Color.GOLD -> 4
                        }
                    )
                )
            }

            for (cross in stripper.crosses.take(3)) {
                val characteristic = stripper.characteristicCells.firstOrNull { cell ->
                    (cell.xRange.start + cell.xRange.endInclusive) / 2 in cross.xRange
                } ?: continue

                add(
                    UpgradableCharacteristic(
                        characteristic = characteristic.characteristic,
                        level = 1,
                    )
                )
            }
        }
    }

    data class CharacteristicCell(
        val characteristic: Characteristic,
        val xRange: ClosedRange<Float>,
    )

    data class Cross(
        val xRange: ClosedRange<Float>,
    )

    private inner class TextStripper : PdfTextStripper() {
        val characteristicCells = mutableListOf<CharacteristicCell>()
        val crosses = mutableListOf<Cross>()

        init {
            setSortByPosition(true)
        }

        override fun onPageEnter() {
        }

        override fun onTextLine(text: String, textPositions: List<TextPosition>) {
            val position = textPositions[0]
            val type = structure.resolveToken(
                TextToken(
                    text = position.getUnicode(),
                    fontName = position.getFont().getName(),
                    height = position.getHeight(),
                    fontSizePt = position.getFontSizeInPt(),
                    y = position.getY(),
                )
            )

            if (type is Token.TableHeadCell || type is Token.TableHeading) {
                val characteristic = CHARACTERISTICS[text.lowercase()] ?: return

                characteristicCells += CharacteristicCell(
                    characteristic,
                    textPositions[0].getX()..textPositions.last().getEndX(),
                )
            }

            if (type is Token.CrossIcon) {
                crosses += Cross(position.getX()..position.getEndX())
            }
        }

        override fun onFinish() {
        }
    }

    data class UpgradableCharacteristic(
        val characteristic: Characteristic,
        val level: Int,
    )

    private inner class RectangleProcessor(page: Page) : RectangleFinder(page) {
        val rectangles = mutableListOf<Rectangle>()

        override fun appendRectangle(
            points: List<Pair<Double, Double>>,
            components: List<Float>
        ) {
            val color = COLORS[components] ?: return

            rectangles += Rectangle(
                points = points,
                color = color,
            )
        }
    }

    companion object {
        private val COLORS = mapOf(
            // CMYK colors
            listOf(0.00f, 0.41f, 0.60f, 0.25f) to Color.BRONZE,
            listOf(0f, 0.07f, 1f, 0f) to Color.GOLD,
            listOf(0f, 0f, 0f, 0.25f) to Color.SILVER,

            // RGB 0-1 colors
            listOf(0.779f, 0.785f, 0.793f) to Color.SILVER,
            listOf(0.765f, 0.515f, 0.346f) to Color.BRONZE,
            listOf(1.0f, 0.889f, 0.0f) to Color.GOLD,
        )

        val CHARACTERISTICS = mapOf(
            "agi" to Characteristic.AGILITY,
            "ag" to Characteristic.AGILITY,
            "bs" to Characteristic.BALLISTIC_SKILL,
            "dex" to Characteristic.DEXTERITY,
            "i" to Characteristic.INITIATIVE,
            "int" to Characteristic.INTELLIGENCE,
            "fel" to Characteristic.FELLOWSHIP,
            "s" to Characteristic.STRENGTH,
            "t" to Characteristic.TOUGHNESS,
            "ws" to Characteristic.WEAPON_SKILL,
            "wp" to Characteristic.WILL_POWER,
        )
    }
}
