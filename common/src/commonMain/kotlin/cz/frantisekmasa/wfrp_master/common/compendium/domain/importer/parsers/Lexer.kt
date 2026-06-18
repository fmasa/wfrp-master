package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

interface Lexer {
    fun getTokens(page: Int): Sequence<Token>
}