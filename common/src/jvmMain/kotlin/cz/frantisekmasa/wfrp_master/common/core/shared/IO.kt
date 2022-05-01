package cz.frantisekmasa.wfrp_master.common.core.shared

import kotlinx.coroutines.Dispatchers

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual val Dispatchers.IO get() = Dispatchers.IO
