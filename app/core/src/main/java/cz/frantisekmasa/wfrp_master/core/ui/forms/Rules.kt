package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.R

private typealias ValidationRule = Pair<(value: String) -> Boolean, String>

class Rules(private vararg val rules: ValidationRule) {
    companion object {
        val NoRules = Rules()

        @Composable
        fun NotBlank(): ValidationRule =
            { v: String -> v.isNotBlank() } to stringResource(R.string.error_cannot_be_empty)
    }

    /**
     * Returns error message if text value is invalid or null if it's valid
     */
    fun errorMessage(value: String): String? =
        rules.firstOrNull { (validator, _) -> !validator(value) }?.second
}