package nyx.sdui.model

sealed class Result<out T> {
    data class Success<out R>(val value: R) : Result<R>()
    data class Failure(val e: Exception) : Result<Nothing>()
}
