package nyx.sdui.components.base

import kotlinx.serialization.Serializable

@Serializable
data class CAction(
    var click: String? = null,
    var navigate: String? = null,
    var select: String? = null
)
