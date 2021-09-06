package nyx.sdui

import nyx.sdui.components.base.Component

sealed class Status<out T> {
    class Loading<out T> : Status<T>()
    data class Success(val layout: Component) : Status<Component>()
    data class Failure(val exception: Exception) : Status<Exception>()
}