package cz.frantisekmasa.wfrp_master.core.domain.rolls

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

class InvalidRollExpression(cause: Throwable?) : Exception(cause)

interface RollExpression {
    companion object {
        fun fromString(text: String, constants: Map<String, Int> = emptyMap()): RollExpression =
            try {
                RollExpressionGrammar(constants).parseToEnd(text)
            } catch (e: ParseException) {
                throw InvalidRollExpression(e)
            }
    }

    fun evaluate(): Int
}

private class DiceRoll(sides: Int) : RollExpression {
    private val dice = Dice(sides)

    override fun evaluate() = dice.roll()
}

private class Multiplication(
    private val a: RollExpression,
    private val b: RollExpression,
) : RollExpression {
    override fun evaluate() = a.evaluate() * b.evaluate()
}

private class Division(
    private val dividend: RollExpression,
    private val divisor: RollExpression,
) : RollExpression {
    override fun evaluate(): Int {
        val divisor = divisor.evaluate()

        if (divisor == 0) {
            return 0
        }

        return dividend.evaluate() / divisor
    }
}

private class Addition(
    private val a: RollExpression,
    private val b: RollExpression,
) : RollExpression {
    override fun evaluate() = a.evaluate() + b.evaluate()
}

private class Subtraction(
    private val a: RollExpression,
    private val b: RollExpression,
) : RollExpression {
    override fun evaluate() = a.evaluate() - b.evaluate()
}

private class IntegerLiteral(private val value: Int) : RollExpression {
    override fun evaluate() = value
}

private class RollExpressionGrammar(val constants: Map<String, Int>) : Grammar<RollExpression>() {
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

    override val rootParser: Parser<RollExpression> by leftAssociative(
        multiplicationOrDivision,
        plus or minus
    ) { l, operator, r -> if (operator.type == plus) Addition(l, r) else Subtraction(l, r) }
}