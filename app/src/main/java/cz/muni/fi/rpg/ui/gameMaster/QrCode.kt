package cz.muni.fi.rpg.ui.gameMaster

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
@Composable
fun QrCode(data: String) {
    WithConstraints(Modifier.fillMaxWidth()) {
        val qrCode = remember { mutableStateOf<ImageAsset?>(null) }
        val width = constraints.maxWidth

        launchInComposition(data, width) {
            qrCode.value = createQrCode(data, width)
        }

        when (val asset = qrCode.value) {
            null -> {
                Box(Modifier.fillMaxWidth().aspectRatio(1f), gravity = ContentGravity.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Image(asset)
            }
        }
    }
}

private suspend fun createQrCode(data: String, size: Int): ImageAsset {
    return withContext(Dispatchers.Default) {
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        bitMatrixToBitmap(writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints))
            .asImageAsset()
    }
}

private fun bitMatrixToBitmap(matrix: BitMatrix): Bitmap {
    val bitmap = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.RGB_565)

    for (x in 0 until matrix.width) {
        for (y in 0 until matrix.height) {
            bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }

    return bitmap
}