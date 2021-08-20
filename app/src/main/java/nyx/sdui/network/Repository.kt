package nyx.sdui.network

import io.ktor.client.request.*
import nyx.sdui.model.LayoutResponse
import nyx.sdui.model.UserEntity

object Repository {

    private val client = ktorHttpClient

    val END_POINT_GET_USER_KTOR = ""
    val END_POINT_POST_USER_KTOR = ""

    suspend fun getUserKtor(
        userId: String
    ): LayoutResponse = client.get("$END_POINT_GET_USER_KTOR$userId")

    suspend fun saveUser(user: UserEntity) {
        client.post<UserEntity>(END_POINT_POST_USER_KTOR) {
            body = user
        }
    }


}