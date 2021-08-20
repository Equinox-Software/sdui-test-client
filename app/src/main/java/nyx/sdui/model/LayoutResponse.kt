package nyx.sdui.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import nyx.sdui.LayoutType

@Serializable
data class LayoutResponse(
    val type: LayoutType,
    val data: String,
    @Polymorphic val onClick: (() -> Unit)? = null
)

