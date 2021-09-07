package nyx.sdui.network

import android.util.Log
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import nyx.sdui.components.base.Component

object Repository {

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