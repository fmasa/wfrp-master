package cz.muni.fi.rpg.ui.joinParty

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.concurrent.Executors


@Composable
internal fun QrCodeScanner(
    onSuccessfulScan: (qrCodeData: String) -> Unit,
    modifier: Modifier,
) {
    val lifecycleOwner = AmbientLifecycleOwner.current
    val context = AmbientContext.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraBound by remember { mutableStateOf(false) }
    val executor by remember { lazy { Executors.newSingleThreadExecutor() } }

    AndroidView(
        modifier = modifier,
        viewBlock = {
            val view = PreviewView(context)
                .apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER

                    // Since Jetpack Compose Alpha 11 there is a regression causing distortion
                    // of SurfaceView rendering.
                    // see https://kotlinlang.slack.com/archives/CJLTWPH7S/p1612283410237200
                    // This forces use of TextureView
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

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
                        Timber.e(e, "Camera binding failed")
                    }
                },
                ContextCompat.getMainExecutor(context)
            )

            view
        },
    )

    DisposableEffect(Unit) {
        onDispose {
            if (cameraBound) {
                cameraProviderFuture.get().unbindAll()
            }
        }
    }
}
