package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Composable
fun vectorResource(@DrawableRes id: Int) = ImageVector.vectorResource(id)