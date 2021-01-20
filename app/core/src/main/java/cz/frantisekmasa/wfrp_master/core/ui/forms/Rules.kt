package cz.frantisekmasa.wfrp_master.core.ui.forms

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.R

class Rules(private vararg val rules: Rule) {
    companion object {
        val NoRules = Rules()

        @Composable
        fun NotBlank() = Rule(R.string.error_cannot_be_empty) { it.isNotBlank() }
    }

    /**
     * Returns error message if text value is invalid or null if it's valid
     */
    fun errorMessage(value: String): String? =
        rules.firstOrNull { !it.validate(value) }?.errorMessage
}

data class Rule(
    val errorMessage: String,
    val validate: (String) -> Boolean,
)

@SuppressLint("ComposableNaming")
@Composable
fun Rule(@StringRes errorMessageResource: Int, validate: (String) -> Boolean) = Rule(
    stringResource(errorMessageResource),
    validate,
)