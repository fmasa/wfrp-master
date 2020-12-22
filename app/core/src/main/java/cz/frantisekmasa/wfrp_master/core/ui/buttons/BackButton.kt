package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import cz.frantisekmasa.wfrp_master.core.R

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) { Icon(vectorResource(R.drawable.ic_navigate_back)) }
}