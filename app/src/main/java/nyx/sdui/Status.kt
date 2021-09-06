package nyx.sdui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import nyx.sdui.components.base.Component

sealed class Status<out T> {
    class Loading<out T> : Status<T>()
    data class Success(val page: Page) : Status<Page>()
    data class Failure(val exception: Exception) : Status<Exception>()
}

@Serializable
data class Page(val layout: Component, val data:  CData)

@Serializable
sealed class CData {
    data class CString(val value: String) : CData()
    data class CInt(val value: Int) : CData()
    data class CBoolean(val value: Boolean) : CData()
    data class CLong(val value: Long) : CData()
    data class CList(val list: List<String>) : CData()
    data class CMap(val map: Map<String, String>) : CData()
}