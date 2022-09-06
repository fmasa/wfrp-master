package cz.frantisekmasa.wfrp_master.desktop.interop

import cz.frantisekmasa.wfrp_master.common.core.shared.File
import cz.frantisekmasa.wfrp_master.common.core.shared.FileChooseListener
import cz.frantisekmasa.wfrp_master.common.core.shared.FileChooser
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.io.FilenameFilter
import javax.swing.JFrame

class NativeFileChooser(
    private val coroutineScope: CoroutineScope,
    private val onFileChoose: FileChooseListener,
) : FileChooser {
    override fun open(type: FileType) {
        val dialog = FileDialog(JFrame("Select a file")).apply {
            isVisible = true

            filenameFilter = FilenameFilter { _, fileName -> allowedExtensions(type).any { fileName.endsWith(it) } }
        }

        val file = dialog.files.firstOrNull()

        coroutineScope.launch {
            onFileChoose(
                when (file) {
                    null -> Result.failure(Exception("File not selected"))
                    else -> Result.success(File(file.inputStream()))
                }
            )
        }
    }

    private fun allowedExtensions(fileType: FileType): List<String> = when (fileType) {
        FileType.PDF -> listOf(".pdf")
        FileType.IMAGE -> listOf(".jpg", ".jpeg", ".png", ".gif")
    }
}
