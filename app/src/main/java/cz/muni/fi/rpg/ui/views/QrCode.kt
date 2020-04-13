package cz.muni.fi.rpg.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.view_qr_code.view.*
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QrCode(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var currentWidth: Int = 0

    private val writer = QRCodeWriter()

    init {
        inflate(getContext(), R.layout.view_qr_code, this)
    }

    public override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        currentWidth = width
    }

    suspend fun drawCode(contents: String) {
        withContext(Dispatchers.Main) {
            progress.visibility = View.VISIBLE
            qrCodeImage.visibility = View.INVISIBLE

            val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
            qrCodeImage.setImageBitmap(
                withContext(Dispatchers.Default) {
                    bitMatrixToBitmap(
                        writer.encode(contents, BarcodeFormat.QR_CODE, width, width, hints)
                    )
                }
            )

            progress.visibility = View.INVISIBLE
            qrCodeImage.visibility = View.VISIBLE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
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
}
