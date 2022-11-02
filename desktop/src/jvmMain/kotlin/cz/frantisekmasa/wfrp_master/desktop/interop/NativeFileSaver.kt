package cz.frantisekmasa.wfrp_master.desktop.interop

import cz.frantisekmasa.wfrp_master.common.core.shared.FileLocationListener
import cz.frantisekmasa.wfrp_master.common.core.shared.FileSaver
import cz.frantisekmasa.wfrp_master.common.core.shared.WriteableFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.io.File
import javax.swing.JFrame

class NativeFileSaver(
    private val coroutineScope: CoroutineScope,
    private val onFileChoose: FileLocationListener,
) : FileSaver {
    override fun selectLocation() {
        val dialog = FileDialog(JFrame("Save file"), "Save file", FileDialog.SAVE).apply {
            isVisible = true
        }

        val directory = dialog.directory
        val fileName = dialog.file

        val file = if (directory.isNullOrBlank() || fileName.isNullOrBlank())
            null
        else File("$directory/$fileName")

        coroutineScope.launch {
            onFileChoose(
                when (file) {
                    null -> Result.failure(Exception("File not selected"))
                    else -> Result.success(WriteableFile(file))
                }
            )
        }
    }
}
