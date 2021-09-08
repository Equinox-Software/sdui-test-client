package nyx.sdui.components.base

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CStyle(
    var padding: List<Int>? = null,
    var color: Long = 0xFF000000,
    var width: Int? = null,
    var height: Int? = null
)