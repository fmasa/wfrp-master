package cz.muni.fi.rpg.ui.gameMaster

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun QrCode(data: String) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        var qrCode: ImageBitmap? by remember { mutableStateOf(null) }
        val width = constraints.maxWidth

        LaunchedEffect(data, width) {
            qrCode = createQrCode(data, width)
        }

        when (val bitmap = qrCode) {
            null -> {
                Box(Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Image(bitmap, stringResource(R.string.image_invitation_qr_code))
            }
        }
    }
}

private suspend fun createQrCode(data: String, size: Int): ImageBitmap {
    return withContext(Dispatchers.Default) {
        val writer = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        bitMatrixToBitmap(writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints))
            .asImageBitmap()
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