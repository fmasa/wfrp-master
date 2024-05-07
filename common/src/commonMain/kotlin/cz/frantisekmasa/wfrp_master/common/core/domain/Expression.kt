package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separated
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.h0tk3y.betterParse.parser.Parser
import cz.frantisekmasa.wfrp_master.common.core.domain.rolls.Dice
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

class InvalidExpression(cause: Throwable?) : Exception(cause)

@Immutable
interface Expression : Parcelable {
    companion object {
        private val CONSTANT_REGEX = Regex("(?<![a-zA-Z0-9])[a-zA-Z]+(?![a-zA-Z0-9])")

        fun fromString(
            text: String,
            constants: Map<String, Int> = emptyMap(),
        ): Expression =
            try {
                RollExpressionGrammar(constants).parseToEnd(text)
            } catch (e: ParseException) {
                throw InvalidExpression(e)
            }

        fun substituteConstants(
            expression: String,
            substitutions: Map<String, String>,
        ): String {
            return expression.replace(CONSTANT_REGEX) { substitutions[it.value] ?: it.value }
        }

        @Composable
        @Stable
        inline fun <reified T> formatter(): (String) -> String where T : Enum<T>, T : Constant {
            val substitutions = enumValues<T>().associate { it.value to it.localizedName }

            return remember(substitutions) {
                { substituteConstants(it, substitutions) }
            }
        }

        @Composable
        @Stable
        inline fun <reified T> normalizer(): (String) -> String where T : Enum<T>, T : Constant {
            val substitutions = enumValues<T>().associate { it.localizedName to it.value }

            return remember(substitutions) {
                { substituteConstants(it.trim(), substitutions) }
            }
        }
    }

    @Immutable
    interface Constant : NamedEnum {
        val value: String
    }

    fun evaluate(): Int

    /**
     * Returns true if calls to [evaluate] always return the same value.
     */
    fun isDeterministic(): Boolean
}

@Parcelize
@Immutable
private data class DiceRoll(
    private val count: Int,
    private val sides: Int,
) : Expression {
    override fun evaluate(): Int {
        val dice = Dice(sides)

        return (0 until count).sumOf { dice.roll() }
    }

    override fun isDeterministic() = false

    override fun toString(): String = "${count}d$sides"
}

@Parcelize
@Immutable
private data class Multiplication(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() * b.evaluate()

    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()

    override fun toString(): String = "$a × $b"
}

@Parcelize
@Immutable
private data class Division(
    private val dividend: Expression,
    private val divisor: Expression,
) : Expression {
    override fun evaluate(): Int {
        val divisor = divisor.evaluate()

        if (divisor == 0) {
            return 0
        }

        return dividend.evaluate() / divisor
    }

    override fun isDeterministic() = dividend.isDeterministic() && divisor.isDeterministic()

    override fun toString(): String = "$dividend ÷ $divisor"
}

@Parcelize
@Immutable
private data class Addition(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() + b.evaluate()

    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()

    override fun toString(): String = "$a + $b"
}

@Parcelize
@Immutable
private data class Subtraction(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() - b.evaluate()

    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()

    override fun toString(): String = "$a - $b"
}

@Parcelize
@Immutable
private data class IntegerLiteral(private val value: Int) : Expression {
    override fun evaluate() = value

    override fun isDeterministic() = true

    override fun toString(): String = "$value"
}

@Parcelize
@Immutable
private data class MaxFunction(private val expressions: List<Expression>) : Expression {
    override fun evaluate(): Int = expressions.maxOf { it.evaluate() }

    override fun isDeterministic(): Boolean = expressions.all { it.isDeterministic() }
}

@Parcelize
@Immutable
private data class MinFunction(private val expressions: List<Expression>) : Expression {
    override fun evaluate(): Int = expressions.minOf { it.evaluate() }

    override fun isDeterministic(): Boolean = expressions.all { it.isDeterministic() }
}

private class RollExpressionGrammar(val constants: Map<String, Int>) : Grammar<Expression>() {
    @Suppress("unused")
    val whitespace by regexToken("\\s+", ignore = true)
    val dice by regexToken("[0-9]+d[1-9][0-9]*")
    val maxFunctionName by regexToken(Regex("MAX", RegexOption.IGNORE_CASE))
    val minFunctionName by regexToken(Regex("MIN", RegexOption.IGNORE_CASE))
    val constant by regexToken(
        Regex(
            constants.keys.sortedByDescending { it.length }.joinToString("|"),
            RegexOption.IGNORE_CASE,
        ),
    )
    val integer by regexToken("([1-9][0-9]*)|0")
    val multiply by literalToken("*")
    val divide by literalToken("/")
    val plus by literalToken("+")
    val minus by literalToken("-")
    val leftParenthesis by literalToken("(")
    val rightParenthesis by literalToken(")")
    val comma by literalToken(",")

    private val diceTerm by dice use {
        val (count, sides) = text.split('d')
        DiceRoll(count.toInt(), sides.toInt())
    }

    val term by diceTerm or
        (integer use { IntegerLiteral(text.toInt()) }) or
        (constant use { IntegerLiteral(constants.getValue(text)) }) or
        (
            skip(maxFunctionName) *
                skip(leftParenthesis) *
                skip(optional(whitespace)) *
                separated(
                    skip(optional(whitespace)) *
                        parser(this::rootParser) *
                        skip(optional(whitespace)),
                    comma,
                ).map { MaxFunction(it.terms) } *
                skip(rightParenthesis)
        ) or
        (
            skip(minFunctionName) *
                skip(leftParenthesis) *
                skip(optional(whitespace)) *
                separated(
                    skip(optional(whitespace)) *
                        parser(this::rootParser) *
                        skip(optional(whitespace)),
                    comma,
                ).map { MinFunction(it.terms) } *
                skip(rightParenthesis)
        ) or
        (skip(leftParenthesis) * parser(this::rootParser) * skip(rightParenthesis))

    val multiplicationOrDivision by leftAssociative(term, multiply or divide) { l, operator, r ->
        if (operator.type == multiply) Multiplication(l, r) else Division(l, r)
    }

    override val rootParser: Parser<Expression> by leftAssociative(
        multiplicationOrDivision,
        plus or minus,
    ) { l, operator, r -> if (operator.type == plus) Addition(l, r) else Subtraction(l, r) }
}
