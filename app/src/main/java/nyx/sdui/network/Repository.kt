package nyx.sdui.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import nyx.sdui.components.base.Component
import nyx.sdui.model.BackendError
import nyx.sdui.model.RouteTokenResponse
import nyx.sdui.model.UserLogin


object Repository {

    val TAG = "REPO"
    lateinit var prefs: SharedPreferences

    fun getUserLogin(context: Context): UserLogin? {

        Log.e(TAG, "--- INIT ---")


        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            "prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );


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

    suspend fun logIn(user: UserLogin): Any {
        val response: HttpResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return try {
            response.body<RouteTokenResponse>()
        } catch (e: Exception) {
            try {
                response.body<BackendError>()
            } catch (e: Exception) {
                e
            }
        }
    }

    //TODO change url to actually create a user
    suspend fun signUp(user: UserLogin): Any {
        saveUserCredentials(user)

        //TODO encrypt password and mail!!!
        val response: HttpResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return try {
            response.body<RouteTokenResponse>()
        } catch (e: Exception) {
            try {
                response.body<BackendError>()
            } catch (e: Exception) {
                e
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString("userLogin_TOKEN", token).apply()
    }

    fun getToken() = prefs.getString("userLogin_TOKEN", null)

    private fun saveUserCredentials(user: UserLogin) {
        prefs.edit {
            putString("userLogin_MAIL", user.username)
            putString("userLogin_PASSWORD", user.password)
        }
    }

    suspend fun getContent(route: String, data: Map<String, Any>): Component {
        val response: HttpResponse = client.get("content/$route") {

            data.entries.forEach {
                Log.e(TAG, ">> pageData :::::::::: ${it.key} --- ${it.value}")
            }

            data.values.map { value -> serializeAny(value) }

            contentType(ContentType.Application.Json)
            setBody(data)

        }

        return response.body<Component>()


    }


    //make this appear
    suspend fun performClick(id: String, data: Map<String, Any>): Component {
        val response: HttpResponse = client.post("click${id}") {
            for (i in data.values) {
                Log.e("REPO", i.toString())
            }

            data.values.map { value ->
                when (value) {
                    is String -> Json.encodeToJsonElement(value)
                    is Int -> Json.encodeToJsonElement(value)
                    is Boolean -> Json.encodeToJsonElement(value)
                    is Long -> Json.encodeToJsonElement(value)
                    is List<*> -> Json.encodeToJsonElement(value) //might fail -- why not for each and then serialize entries?

                    else -> throw SerializationException("Unsupported Type! Can't serialize $value.")
                }


            }


            Log.e("REPO", "DATA -- $data")

            Log.e("REPO", "DATA - ID -- ${data[id]}")

            Log.e("REPO", "DATA - ID -- ${data["abTuT"]}")

            contentType(ContentType.Application.Json)
            setBody(data)

        }

        return response.body<Component>()
    }

    fun serializeAny(value: Any): JsonElement? =
        when (value) {
            is String -> Json.encodeToJsonElement(value)
            is Int -> Json.encodeToJsonElement(value)
            is Boolean -> Json.encodeToJsonElement(value)
            is Long -> Json.encodeToJsonElement(value)
            /* is List<*> -> (value as List<Any>).forEach { entry ->
                 entry?.let {
                     serializeAny(it!!)
                 }

                 return value as List
             }

             */
            else -> null

            //might fail -- why not for each and then serialize entries?

            //throw SerializationException("Unsupported Type! Can't serialize $value.")
        }
}
