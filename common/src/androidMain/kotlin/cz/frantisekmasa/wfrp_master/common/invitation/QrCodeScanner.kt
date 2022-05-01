package cz.frantisekmasa.wfrp_master.common.invitation

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.github.aakira.napier.Napier
import java.util.concurrent.Executors


@Composable
internal fun QrCodeScanner(
    onSuccessfulScan: (qrCodeData: String) -> Unit,
    modifier: Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraBound by remember { mutableStateOf(false) }
    val executor by remember { lazy { Executors.newSingleThreadExecutor() } }

    val orientation = LocalConfiguration.current.orientation

    key(orientation) {
        AndroidView(
            modifier = modifier,
            factory = {
                PreviewView(context)
                    .apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER

                        // Since Jetpack Compose Alpha 11 there is a regression causing distortion
                        // of SurfaceView rendering.
                        // see https://kotlinlang.slack.com/archives/CJLTWPH7S/p1612283410237200
                        // This forces use of TextureView
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
            },
            update = { view ->
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder()
                            .build()
                            .apply {
                                setSurfaceProvider(view.surfaceProvider)
                            }

                        @SuppressLint("RestrictedApi")
                        val analysis = ImageAnalysis.Builder()
                            .setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(
                                    executor,
                                    QrCodeScannerAnalyser(onQrCodesDetected = onSuccessfulScan)
                                )
                            }

                        try {
                            cameraProvider.unbindAll()

                            cameraBound = true
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )
                        } catch (e: Exception) {
                            Napier.e("Camera binding failed", e)
                        }
                    },
                    ContextCompat.getMainExecutor(context)
                )
            },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (cameraBound) {
                cameraProviderFuture.get().unbindAll()
            }
        }
    }
}
