package cz.frantisekmasa.wfrp_master.core.ui.primitives

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun longToast(context: Context, @StringRes messageRes: Int) {
    withContext(Dispatchers.Main) {
        Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
    }
}

suspend fun shortToast(context: Context, @StringRes messageRes: Int) {
    withContext(Dispatchers.Main) {
        Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
    }
}
