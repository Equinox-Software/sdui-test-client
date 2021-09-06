package nyx.sdui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import nyx.sdui.components.base.AnySerializer
import nyx.sdui.components.base.Component

sealed class Status<out T> {
    class Loading<out T> : Status<T>()
    data class Success(val page: Page) : Status<Page>()
    data class Failure(val exception: Exception) : Status<Exception>()
}

@Serializable
data class Page(val layout: Component, val data: Map<String,@Serializable(AnySerializer::class) Any>)