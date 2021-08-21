package nyx.sdui.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable


@Serializable
data class LayoutResponse(
    val id: String,
    val type: LayoutType,
    val data: String
)

@Serializable
data class TopLayoutResponse(val id:String,val children: List<LayoutResponse> = emptyList()  ) {


}

