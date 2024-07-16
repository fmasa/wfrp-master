package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

class TokenStream(
    private val tokens: PeekingIterator<Token>,
) {
    constructor(tokens: List<Token>) : this(PeekingIterator(tokens.iterator()))
    constructor(tokens: Sequence<Token>) : this(PeekingIterator(tokens.iterator()))

    var cursor: Int = 0
        private set

    fun consumeWhile(
        max: Int = Int.MAX_VALUE,
        predicate: (Token) -> Boolean,
    ): List<Token> {
        return sequence {
            var consumed = 0

            while (tokens.hasNext() && consumed < max) {
                if (!predicate(tokens.peek()!!)) {
                    return@sequence
                }

                yield(tokens.next())
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
        check(!tokens.hasNext()) {
            "Expected end of content, ${tokens.peek()} found"
        }
    }

    fun peek(): Token? = tokens.peek()
}

class PeekingIterator<T : Any>(
    private val iterator: Iterator<T>,
) : Iterator<T> {
    private var next: T? = if (iterator.hasNext()) iterator.next() else null

    fun peek(): T? = next

    // hasNext() and next() should behave the same as in the Iterator interface.
    // Override them if needed.
    override fun next(): T {
        val result = next ?: throw NoSuchElementException()
        next = if (iterator.hasNext()) iterator.next() else null
        return result
    }

    override fun hasNext(): Boolean {
        return next != null
    }
}
