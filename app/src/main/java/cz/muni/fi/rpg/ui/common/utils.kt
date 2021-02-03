package cz.muni.fi.rpg.ui.common

import android.app.Activity
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import java.io.Serializable

/*
 * Utility extension functions for UI components
 */

inline fun <reified T : Parcelable> Fragment.optionalParcelableArgument(name: String) = lazy {
    requireArguments().getParcelable<T>(name)
}

inline fun <reified T : Serializable> Fragment.serializableArgument(name: String) = lazy {
    val argument = requireArguments().getSerializable(name) ?: error("Argument $name was not passed")

    check(argument is T) { "Argument $argument is not instance of ${T::class.java}"}

    @Suppress("USELESS_CAST") // Kotlin cannot infer this correctly, so it's not really useless
    argument as T
}

@UiThread
fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun <T> List<T>.chunk(itemsInChunk: Int): List<List<T>> {
    val chunks = (0..(size / itemsInChunk)).map { ArrayList<T>(itemsInChunk) }

    for (index in indices) {
        chunks[index / itemsInChunk].add(this[index])
    }

    return chunks
}