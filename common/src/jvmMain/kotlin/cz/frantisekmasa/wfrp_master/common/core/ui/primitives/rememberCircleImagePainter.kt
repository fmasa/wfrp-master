package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.jetbrains.skia.Image
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
internal actual fun rememberImagePainter(url: String): State<Painter> {
    val http: HttpClient by localDI().instance()

    val painter = remember { mutableStateOf<Painter>(NullPainter) }

    LaunchedEffect(url) {
        val response: HttpResponse = http.get(url)

        val body: ByteArray = response.body()
        painter.value = BitmapPainter(Image.makeFromEncoded(body).toComposeImageBitmap())
    }

    return painter
}

private object NullPainter : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified

    override fun DrawScope.onDraw() {}
}
