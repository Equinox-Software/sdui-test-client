package nyx.sdui.components.base

import kotlinx.serialization.Serializable

@Serializable
data class CAction(
    val click: String? = null,
    val navigate: String? = null,
    val select: String? = null,
val keys:Set<String>?=null
)
