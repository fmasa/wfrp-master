package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import androidx.compose.ui.geometry.Size as ComposeSize

object EmptyUI {
    enum class Size {
        /**
         * Should be used, when there are more lists on a screen and this empty UI is only related
         * to part of it
         */
        Small,

        /**
         * Should be used when main content of screen is empty
         */
        Large,

        ;

        val textStyle: TextStyle
            @Composable
            get() =
                when (this) {
                    Small -> MaterialTheme.typography.subtitle1
                    Large -> MaterialTheme.typography.h6
                }

        @Stable
        fun modifier(intrinsicSize: ComposeSize): Modifier =
            when (this) {
                Small -> Modifier.iconSize(60.dp, intrinsicSize).padding(top = 16.dp)
                Large -> Modifier.iconSize(64.dp, intrinsicSize)
            }

        private fun Modifier.iconSize(
            width: Dp,
            intrinsicSize: ComposeSize,
        ): Modifier {
            if (intrinsicSize.width == intrinsicSize.height) {
                return size(width)
            }

            return width(width)
        }
    }
}

@Composable
fun EmptyUI(
    text: String,
    icon: Resources.Drawable,
    subText: String? = null,
    size: EmptyUI.Size = EmptyUI.Size.Large,
) {
    EmptyUI(
        text = text,
        iconPainter = drawableResource(icon),
        subText = subText,
        size = size,
    )
}

@Composable
fun EmptyUI(
    text: String,
    icon: ImageVector,
    subText: String? = null,
    size: EmptyUI.Size = EmptyUI.Size.Large,
) {
    EmptyUI(Modifier, text, rememberVectorPainter(icon), subText, size)
}

@Composable
fun CompactEmptyUI(text: String) {
    EmptyUI(
        modifier = Modifier.padding(top = Spacing.small),
        text = text,
        iconPainter = null,
        subText = null,
        size = EmptyUI.Size.Small,
    )
}

@Composable
fun EmptyUI(
    modifier: Modifier = Modifier,
    text: String,
    iconPainter: Painter?,
    subText: String? = null,
    size: EmptyUI.Size = EmptyUI.Size.Large,
) {
    val disabledColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
        if (size == EmptyUI.Size.Large) {
            Spacer(Modifier.fillMaxHeight(0.35f))
        }

        if (iconPainter != null) {
            Image(
                iconPainter,
                contentDescription = VISUAL_ONLY_ICON_DESCRIPTION,
                modifier = size.modifier(iconPainter.intrinsicSize),
                colorFilter = ColorFilter.tint(disabledColor),
            )
        }

        Text(text, style = size.textStyle, textAlign = TextAlign.Center)

        subText?.let {
            Text(it, textAlign = TextAlign.Center, color = disabledColor)
        }
    }
}
