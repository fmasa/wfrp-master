package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import java.io.InputStream

@Composable
expect fun rememberFileChooser(
    onFileChoose: suspend CoroutineScope.(Result<File>) -> Unit
): FileChooser

expect class File {
    val stream: InputStream

    fun readBytes(): ByteArray
}

interface FileChooser {
    fun open(type: FileType)
}

enum class FileType {
    IMAGE,
    PDF
}
