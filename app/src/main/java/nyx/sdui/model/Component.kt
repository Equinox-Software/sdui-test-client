package nyx.sdui.model

import kotlinx.serialization.Serializable

@Serializable
data class Component(
    val id: String,
    val type: ComponentType,
    val data: String? = null,
    val children: List<Component>? = null
)


