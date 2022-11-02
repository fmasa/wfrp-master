package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream

@Composable
expect fun rememberFileChooser(
    onFileChoose: suspend CoroutineScope.(Result<ReadableFile>) -> Unit
): FileChooser

@Composable
expect fun rememberFileSaver(
    type: FileType,
    defaultFileName: String,
    onLocationChoose: suspend CoroutineScope.(Result<WriteableFile>) -> Unit,
): FileSaver

expect class ReadableFile {
    val stream: InputStream

    fun readBytes(): ByteArray
}

expect class WriteableFile {
    fun writeBytes(bytes: ByteArray)
    fun close()
}

interface FileChooser {
    fun open(type: FileType)
}

fun interface FileSaver {
    fun selectLocation()
}

enum class FileType {
    IMAGE,
    PDF,
    JSON
}
