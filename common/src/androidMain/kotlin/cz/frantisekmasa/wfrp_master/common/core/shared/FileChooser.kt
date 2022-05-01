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

@Composable
actual fun rememberFileChooser(
    onFileChoose: suspend CoroutineScope.(Result<File>) -> Unit
): FileChooser {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        coroutineScope.launch(Dispatchers.IO) {
            if (uri == null) {
                onFileChoose(Result.failure(Exception("URI not selected")))
                return@launch
            }

            val inputStream = context.contentResolver.openInputStream(uri)

            onFileChoose(
                if (inputStream == null)
                    Result.failure(Exception("Could not open input stream"))
                else Result.success(File(inputStream))
            )
        }
    }

    return AndroidFileChooser(launcher)
}

actual class File(
    actual val stream: InputStream
) {
    actual fun readBytes(): ByteArray = stream.readBytes()
}

class AndroidFileChooser(
    private val launcher: ManagedActivityResultLauncher<String, Uri?>
): FileChooser {
    override fun open(type: FileType) {
        launcher.launch(
            when(type) {
                FileType.IMAGE -> "image/*"
                FileType.PDF -> "application/pdf"
            }
        )
    }
}
