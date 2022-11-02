package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.InputStream

typealias FileChooseListener = suspend CoroutineScope.(Result<ReadableFile>) -> Unit
typealias FileLocationListener = suspend CoroutineScope.(Result<WriteableFile>) -> Unit

val LocalFileChooserFactory = staticCompositionLocalOf<(FileChooseListener) -> FileChooser> {
    error("LocalFileChooser was not set")
}

val LocalFileSaverFactory = staticCompositionLocalOf<(FileLocationListener) -> FileSaver> {
    error("LocalFileChooser was not set")
}

@Composable
actual fun rememberFileChooser(onFileChoose: FileChooseListener): FileChooser {
    return LocalFileChooserFactory.current(onFileChoose)
}

@Composable
actual fun rememberFileSaver(
    type: FileType,
    defaultFileName: String,
    onLocationChoose: FileLocationListener,
): FileSaver {
    return LocalFileSaverFactory.current(onLocationChoose)
}

actual class ReadableFile(
    actual val stream: InputStream
) {
    actual fun readBytes(): ByteArray = stream.readBytes()
}

actual class WriteableFile(
    private val file: File,
) {
    actual fun writeBytes(bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    actual fun close() {
    }
}
