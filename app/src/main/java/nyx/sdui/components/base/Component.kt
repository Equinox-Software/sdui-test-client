package nyx.sdui.components.base

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Component(
    val id: String,
    val type: ComponentType,
    //might be better to use more parameters than packing it all into data
    val data: JsonElement? = null,
    val children: List<Component>? = null,
    val actions: Map<ComponentActionType, JsonElement>? = null,
val styles: List<ComponentStyle>?= null
)