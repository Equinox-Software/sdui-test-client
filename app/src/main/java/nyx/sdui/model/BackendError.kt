package nyx.sdui.model

import kotlinx.serialization.Serializable

@Serializable
data class BackendError(val errorCode: Int, val message: String)