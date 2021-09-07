package nyx.sdui.components.base

import kotlinx.serialization.Serializable

@Serializable
data class CStyle(
    var padding: List<Int> = listOf(0, 0, 0, 0),
    var color: Long = 0xFF000000
)