package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

object ContextMenu {
    data class Item(
        val text: String,
        val onClick: () -> Unit,
    )
}
