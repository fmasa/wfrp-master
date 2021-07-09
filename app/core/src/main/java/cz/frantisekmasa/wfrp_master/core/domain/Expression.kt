package cz.frantisekmasa.wfrp_master.core.domain

import android.os.Parcelable
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.or
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
import cz.frantisekmasa.wfrp_master.core.domain.rolls.Dice
import kotlinx.parcelize.Parcelize

class InvalidExpression(cause: Throwable?) : Exception(cause)

interface Expression : Parcelable {
    companion object {
        fun fromString(text: String, constants: Map<String, Int> = emptyMap()): Expression =
            try {
                RollExpressionGrammar(constants).parseToEnd(text)
            } catch (e: ParseException) {
                throw InvalidExpression(e)
            }
    }

    fun evaluate(): Int

    /**
     * Returns true if calls to [evaluate] always return the same value.
     */
    fun isDeterministic(): Boolean
}

@Parcelize
private data class DiceRoll(private val sides: Int) : Expression {
    override fun evaluate() = Dice(sides).roll()
    override fun isDeterministic() = false
    override fun toString(): String = "d$sides"
}

@Parcelize
private data class Multiplication(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() * b.evaluate()
    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()
    override fun toString(): String = "$a × $b"
}

@Parcelize
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
private data class Addition(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() + b.evaluate()
    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()
    override fun toString(): String = "$a + $b"
}

@Parcelize
private data class Subtraction(
    private val a: Expression,
    private val b: Expression,
) : Expression {
    override fun evaluate() = a.evaluate() - b.evaluate()
    override fun isDeterministic() = a.isDeterministic() && b.isDeterministic()
    override fun toString(): String = "$a - $b"
}

@Parcelize
private data class IntegerLiteral(private val value: Int) : Expression {
    override fun evaluate() = value
    override fun isDeterministic() = true
    override fun toString(): String = "$value"
}

private class RollExpressionGrammar(val constants: Map<String, Int>) : Grammar<Expression>() {
    @Suppress("unused")
    val whitespace by regexToken("\\s+", ignore = true)
    val dice by regexToken("[0-9]+d[1-9][0-9]*")
    val constant by regexToken(constants.keys.sortedByDescending { it.length }.joinToString("|"))
    val integer by regexToken("([1-9][0-9]*)|0")
    val multiply by literalToken("*")
    val divide by literalToken("/")
    val plus by literalToken("+")
    val minus by literalToken("-")
    val leftParenthesis by literalToken("(")
    val rightParenthesis by literalToken(")")

    private val diceTerm by dice use {
        val (multiplier, sides) = text.split('d')
        Multiplication(IntegerLiteral(multiplier.toInt()), DiceRoll(sides.toInt()))
    }

    val term by diceTerm or
        (integer use { IntegerLiteral(text.toInt()) }) or
        (constant use { IntegerLiteral(constants.getValue(text)) }) or
        (skip(leftParenthesis) * parser(this::rootParser) * skip(rightParenthesis))

    val multiplicationOrDivision by leftAssociative(term, multiply or divide) { l, operator, r ->
        if (operator.type == multiply) Multiplication(l, r) else Division(l, r)
    }

    override val rootParser: Parser<Expression> by leftAssociative(
        multiplicationOrDivision,
        plus or minus
    ) { l, operator, r -> if (operator.type == plus) Addition(l, r) else Subtraction(l, r) }
}
