package cz.frantisekmasa.wfrp_master.common.core.shared

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

@Composable
actual fun rememberFileChooser(onFileChoose: suspend CoroutineScope.(Result<ReadableFile>) -> Unit): FileChooser {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            coroutineScope.launch(Dispatchers.IO) {
                if (uri == null) {
                    onFileChoose(Result.failure(Exception("URI not selected")))
                    return@launch
                }

                val inputStream = context.contentResolver.openInputStream(uri)

                inputStream.use {
                    onFileChoose(
                        if (inputStream == null) {
                            Result.failure(Exception("Could not open input stream"))
                        } else {
                            Result.success(ReadableFile(inputStream))
                        },
                    )
                }
            }
        }

    return AndroidFileChooser(launcher)
}

@Composable
actual fun rememberFileSaver(
    type: FileType,
    defaultFileName: String,
    onLocationChoose: suspend CoroutineScope.(Result<WriteableFile>) -> Unit,
): FileSaver {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val contract =
        ActivityResultContracts.CreateDocument(
            when (type) {
                FileType.IMAGE -> "image/jpeg"
                FileType.PDF -> "application/pdf"
                FileType.JSON -> "application/json"
            },
        )

    val launcher =
        rememberLauncherForActivityResult(contract) { uri ->
            coroutineScope.launch(Dispatchers.IO) {
                if (uri == null) {
                    onLocationChoose(Result.failure(Exception("URI not selected")))
                    return@launch
                }

                val outputStream = context.contentResolver.openOutputStream(uri)

                onLocationChoose(
                    if (outputStream == null) {
                        Result.failure(Exception("Could not open output stream"))
                    } else {
                        Result.success(WriteableFile(outputStream))
                    },
                )
            }
        }

    return FileSaver {
        launcher.launch(defaultFileName)
    }
}

actual class ReadableFile(
    actual val stream: InputStream,
) {
    actual fun readBytes(): ByteArray = stream.readBytes()
}

actual class WriteableFile(
    private val stream: OutputStream,
) {
    actual fun writeBytes(bytes: ByteArray) {
        stream.write(bytes)
    }

    actual fun close() {
        stream.close()
    }
}

class AndroidFileChooser(
    private val launcher: ManagedActivityResultLauncher<String, Uri?>,
) : FileChooser {
    override fun open(type: FileType) {
        launcher.launch(
            when (type) {
                FileType.IMAGE -> "image/*"
                FileType.PDF -> "application/pdf"
                FileType.JSON -> "application/json"
            },
        )
    }
}
