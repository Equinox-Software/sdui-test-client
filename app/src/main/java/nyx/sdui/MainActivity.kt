package nyx.sdui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nyx.sdui.model.LayoutResponse
import nyx.sdui.ui.theme.SduiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SduiTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
                ) {
                    Greeting("Android")

                    Test()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}



@OptIn(ExperimentalSerializationApi::class)
@Composable
fun Test() {
    val responses = listOf(
        Json.encodeToString(LayoutResponse(LayoutType.TEXT, "Some text goes here")),
        Json.encodeToString(LayoutResponse(LayoutType.BUTTON, "Click me (fix this part lel)",/*{
            Log.i(
                "TAG",
                "----"
            )
        }*/)
        )
    )

    for (resp in responses) {
        val obj = Json.decodeFromString<LayoutResponse>(resp)

        when (obj.type) {
            LayoutType.TEXT -> {
                Text(obj.data.toString())
            }

            LayoutType.BUTTON -> {
                Button({  }) {
                    Text(obj.data.toString())
                }
            }

        }
    }


}

enum class LayoutType {
    TEXT, BUTTON
}