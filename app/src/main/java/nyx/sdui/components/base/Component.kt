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
    val action: CAction? = null,
    val style: CStyle? = CStyle()
)