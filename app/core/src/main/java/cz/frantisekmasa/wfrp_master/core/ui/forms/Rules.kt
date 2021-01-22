package cz.frantisekmasa.wfrp_master.core.ui.forms

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.R

class Rules(private vararg val rules: Rule) : Rule {
    companion object {
        val NoRules = Rules()

        @Composable
        fun NotBlank() = Rule(R.string.error_cannot_be_empty) { it.isNotBlank() }

        @Composable
        fun NonNegativeNumber() = Rule(R.string.error_must_be_non_negative_number) {
            it.toDoubleOrNull() != null && it.toDouble() >= 0
        }

        @Composable
        fun PositiveInteger() = Rule(R.string.error_must_be_positive_int) {
            it.toIntOrNull() != null && it.toInt() > 0
        }

        @Composable
        fun NonNegativeInteger() = Rule(R.string.error_must_be_non_negative_number) {
            it.toIntOrNull() != null && it.toInt() >= 0
        }

        /**
         * This is useful usually for inputs that:
         * - Have small width that doesn't allow us to show full error message
         * - Do not have *unexpected* validation rules for user
         */
        fun withEmptyMessage(rule: Rule) = Rule { if (rule.errorMessage(it) != null) "" else null }
        fun withEmptyMessage(validate: (String) -> Boolean): Rule = CallbackRule("", validate)

        fun IfNotBlank(rule: Rule): Rule = Rule {
            if (it.isBlank()) {
                null
            } else {
                rule.errorMessage(it)
            }
        }
    }

    override fun errorMessage(value: String): String? {
        for (rule in rules) {
            val message = rule.errorMessage(value)

            if (message != null) {
                return message
            }
        }

        return null
    }
}

fun interface Rule {
    /**
     * Returns error message if text value is invalid or null if it's valid
     */
    fun errorMessage(value: String): String?
}

private data class CallbackRule(
    val errorMessage: String,
    val validate: (String) -> Boolean,
) : Rule {
    override fun errorMessage(value: String) = if (validate(value)) null else errorMessage
}

@SuppressLint("ComposableNaming")
@Composable
fun Rule(@StringRes errorMessageResource: Int, validate: (String) -> Boolean): Rule = CallbackRule(
    stringResource(errorMessageResource),
    validate,
)