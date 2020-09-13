package cz.muni.fi.rpg.ui.common

import android.app.Activity
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.io.Serializable

/*
 * Utility extension functions for UI components
 */

fun View.toggleVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun Fragment.stringArgument(name: String, default: String? = null) = lazy {
    requireArguments().getString(name) ?: default ?: error("Argument $name was not passed")
}

inline fun <reified T : Parcelable> Fragment.parcelableArgument(name: String) = lazy {
    requireArguments().getParcelable<T>(name) ?: error("Argument $name was not passed")
}

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

@UiThread
fun Activity.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

@UiThread
fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.let { Toast.makeText(it, message, duration).show() }
}

@UiThread
fun Fragment.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    context?.let { Toast.makeText(it, message, duration).show() }
}