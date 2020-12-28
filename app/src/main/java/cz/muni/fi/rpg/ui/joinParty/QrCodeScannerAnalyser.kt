package cz.muni.fi.rpg.ui.joinParty

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import timber.log.Timber

class QrCodeScannerAnalyser(
    private val onQrCodesDetected: (qrCodeData: String) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private val reader = QRCodeReader()
    }

    override fun analyze(imageProxy: ImageProxy) {
        try {
            @SuppressLint("UnsafeExperimentalUsageError")
            val image = imageProxy.image ?: return

            image.cropRect

            if (image.format != ImageFormat.YUV_420_888) {
                Timber.e("Unknown image format")
                return
            }

            if (image.planes.size != 3) {
                Timber.e("Invalid number of image planes (${image.planes.size})")
                return
            }

            // Luminance plane
            val buffer = image.planes[0].buffer;
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)

            Timber.d("Creating bitmap for image (${image.width}x${image.height})")

            val binaryBitmap = BinaryBitmap(
                HybridBinarizer(
                    PlanarYUVLuminanceSource(
                        bytes,
                        image.width,
                        image.height,
                        0,
                        0,
                        image.width,
                        image.height,
                        false
                    )
                )
            )

            val result = reader.decode(binaryBitmap)
            onQrCodesDetected(result.text)
        } catch (e: NotFoundException) {
            Timber.d(e)
        } finally {
            imageProxy.close()
        }
    }
}