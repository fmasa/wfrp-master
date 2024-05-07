package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VISUAL_ONLY_ICON_DESCRIPTION

@Composable
fun Breadcrumbs(content: BreadcrumbsScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val scope = BreadcrumbsScopeImpl()
            scope.content()
            val navigation = LocalNavigationTransaction.current

            scope.levels.forEachIndexed { index, breadcrumbLevel ->
                key(index) {
                    if (index != 0) {
                        Icon(Icons.Rounded.ArrowForwardIos, VISUAL_ONLY_ICON_DESCRIPTION)
                    }

                    val destination = breadcrumbLevel.destination

                    if (destination != null) {
                        TextButton(
                            onClick = {
                                val key = destination().key
                                navigation.goBackTo { it.key == key }
                            },
                        ) {
                            Text(breadcrumbLevel.label)
                        }
                    } else {
                        Text(breadcrumbLevel.label, style = MaterialTheme.typography.button)
                    }
                }
            }
        }
    }
}

interface BreadcrumbsScope {
    fun level(
        label: String,
        destination: (() -> Screen)? = null,
    )
}

private class BreadcrumbsScopeImpl : BreadcrumbsScope {
    val levels = mutableListOf<BreadcrumbLevel>()

    override fun level(
        label: String,
        destination: (() -> Screen)?,
    ) {
        this.levels.add(BreadcrumbLevel(label, destination))
    }
}

data class BreadcrumbLevel(
    val label: String,
    val destination: (() -> Screen)?,
)
