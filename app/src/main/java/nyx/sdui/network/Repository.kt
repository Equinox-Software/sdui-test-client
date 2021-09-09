package nyx.sdui.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import nyx.sdui.components.base.Component
import nyx.sdui.model.RouteTokenResponse
import nyx.sdui.model.UserLogin

object Repository {

    val TAG = "REPO"
    lateinit var prefs: SharedPreferences

    fun getUserLogin(context: Context): UserLogin? {

        Log.e(TAG, "--- INIT ---")
        prefs = EncryptedSharedPreferences.create(
            "prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val mail = prefs.getString("userLogin_MAIL", null)
        val password = prefs.getString("userLogin_PASSWORD", null)

        return if (mail != null && password != null) {
            Log.e(TAG, "--- INIT ---")
            UserLogin(mail, password)
        } else {
            Log.e(TAG, "--- INIT null ---")
            null
        }
    }

    suspend fun logIn(user: UserLogin): RouteTokenResponse {
        saveUserCredentials(user)

        return client.post("/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            body = user
        }
    }

    //TODO cahnge url to actually create a user
    suspend fun signUp(user: UserLogin): RouteTokenResponse = client.post("/auth/login") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        body = user
    }

    fun saveToken(token: String) {
        prefs.edit().putString("userLogin_TOKEN", token).apply()
    }

    fun getToken() = prefs.getString("userLogin_TOKEN", null)

    fun saveUserCredentials(user: UserLogin) {
        prefs.edit {
            putString("userLogin_MAIL", user.username)
            putString("userLogin_PASSWORD", user.password)
        }
    }

    suspend fun getContent(route: String): Component = client.get("content/$route")


    //make this appear
    suspend fun performClick(id: String, data: Map<String, Any>): Component =
        client.post("click${id}") {
            for (i in data.values) {
                Log.e("REPO", i.toString())
            }

            data.values.map { value ->
                when (value) {
                    is String -> Json.encodeToJsonElement(value)
                    is Int -> Json.encodeToJsonElement(value)
                    is Boolean -> Json.encodeToJsonElement(value)
                    is Long -> Json.encodeToJsonElement(value)
                    is List<*> -> Json.encodeToJsonElement(value) //might fail

                    else -> throw SerializationException("Unsupported Type! Can't serialize $value.")
                }


            }


            Log.e("REPO", "DATA -- $data")

            Log.e("REPO", "DATA - ID -- ${data[id]}")

            Log.e("REPO", "DATA - ID -- ${data["abTuT"]}")

            contentType(ContentType.Application.Json)
            body = data

        }
}