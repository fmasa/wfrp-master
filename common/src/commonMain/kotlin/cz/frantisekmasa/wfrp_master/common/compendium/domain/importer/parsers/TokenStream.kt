package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

class TokenStream(
    private val tokens: List<Token>,
) {
    var cursor: Int = 0
        private set

    fun consumeWhile(
        max: Int = Int.MAX_VALUE,
        predicate: (Token) -> Boolean,
    ): List<Token> {
        return sequence {
            var consumed = 0

            while (cursor <= tokens.lastIndex && consumed < max) {
                if (!predicate(tokens[cursor])) {
                    return@sequence
                }

                yield(tokens[cursor])
                cursor++
                consumed++
            }
        }.toList()
    }

    inline fun <reified T : Token> consumeWhileOfType(): List<T> {
        @Suppress("UNCHECKED_CAST")
        return consumeWhile { it is T } as List<T>
    }

    fun consumeOne(): Token {
        return consumeOneOfType()
    }

    inline fun <reified T : Token> consumeOneOfType(): T {
        val tokens = consumeWhile(max = 1) { it is T }
        check(tokens.size == 1) { "Expected ${T::class.simpleName} on index $cursor, ${peek()} given" }

        return tokens[0] as T
    }

    fun consumeUntil(predicate: (Token) -> Boolean): List<Token> {
        return consumeWhile { !predicate(it) }
    }

    fun dropWhile(predicate: (Token) -> Boolean) {
        consumeWhile(predicate = predicate)
    }

    fun dropUntil(predicate: (Token) -> Boolean) {
        consumeUntil(predicate)
    }

    fun assertEnd() {
        check(cursor > tokens.lastIndex) {
            "Expected end of content, ${tokens[cursor]} found"
        }
    }

    fun peek(): Token? {
        return tokens.getOrNull(cursor)
    }
}
