package nyx.sdui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("currentRank")
    val currentRank: Int,
    @SerialName("totalStars")
    val totalStars: Int,
    @SerialName("totalWordsMastered")
    val totalWordsMastered: Int,
)
