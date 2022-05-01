package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream

typealias FileChooseListener = suspend CoroutineScope.(Result<File>) -> Unit

val LocalFileChooserFactory = staticCompositionLocalOf<(FileChooseListener) -> FileChooser> {
    error("LocalFileChooser was not set")
}

@Composable
actual fun rememberFileChooser(onFileChoose: FileChooseListener): FileChooser {
    return LocalFileChooserFactory.current(onFileChoose)
}

actual class File(
    actual val stream: InputStream
) {
    actual fun readBytes(): ByteArray = stream.readBytes()
}


