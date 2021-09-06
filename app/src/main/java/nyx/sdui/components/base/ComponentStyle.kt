package nyx.sdui.components.base

import kotlinx.serialization.Serializable

@Serializable
sealed class ComponentStyle(val type:ComponentStyleType) {
    data class Padding(val start: Int, val top: Int,  val end: Int, val bottom: Int) : ComponentStyle(ComponentStyleType.PADDING)
    data  class Color(val color: Long) : ComponentStyle(ComponentStyleType.COLOR)
}
