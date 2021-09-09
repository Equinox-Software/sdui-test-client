package nyx.sdui.model

import kotlinx.serialization.Serializable

@Serializable
data class UserLogin(val username: String, val password: String)