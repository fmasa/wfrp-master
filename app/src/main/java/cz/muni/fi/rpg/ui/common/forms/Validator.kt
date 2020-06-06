package cz.muni.fi.rpg.ui.common.forms

import android.text.Editable

internal class Validator(
    val errorMessage: String,
    val rule: (Editable?) -> Boolean,
    val isLiveValidationSupported: Boolean
)