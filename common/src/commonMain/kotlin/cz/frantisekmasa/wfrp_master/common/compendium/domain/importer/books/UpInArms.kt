package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TalentParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass

object UpInArms : Book, CareerSource, TalentSource {

    override val name = "Up in Arms"

    override fun importCareers(document: Document): List<Career> {
        return CareerParser().import(
            document,
            this,
            sequenceOf(
                SocialClass.WARRIORS to listOf(10, 12, 14, 16),
                SocialClass.ACADEMICS to listOf(18),
                SocialClass.RANGERS to listOf(20),
                SocialClass.ACADEMICS to listOf(22),
                SocialClass.WARRIORS to listOf(31, 32, 34, 36, 44, 46, 78),
            ),
        )
    }

    override fun importTalents(document: Document): List<Talent> {
        return TalentParser().import(document, this, (140..141).asSequence())
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            return Token.BoxHeader(textToken.text)
        }

        if (textToken.fontSizePt == 19f && textToken.fontName.endsWith("CaslonAntique-Bold")) {
            return Token.Heading1(textToken.text)
        }

        if (textToken.fontSizePt == 12f && textToken.fontName.endsWith("ACaslonPro-Bold")) {
            return Token.Heading3(textToken.text)
        }

        if (textToken.fontSizePt == 10f || textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                return Token.BoldPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken.text)
            }
            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                return Token.NormalPart(textToken.text)
            }
        }

        return null
    }
}
