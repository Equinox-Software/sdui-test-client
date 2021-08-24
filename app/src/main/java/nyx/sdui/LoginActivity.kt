package nyx.sdui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import nyx.sdui.network.ktorHttpClient
import nyx.sdui.ui.theme.SduiTheme

class LoginActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SduiTheme {
                /*  when (val result = viewModel.result.collectAsState().value) {
                      is Status.Loading -> {
                          Log.w(TAG, "Loading")
                          LoadingScreen()
                      }

                      is Status.Success -> {
                          Log.w(TAG, "Success")
                         Content(result.data as Component)
                      }

                      is Status.Failure -> {
                          val e = result.exception as Exception

                          Log.e(
                              TAG,
                              "Loading $TAG failed: ${e.message}\n\n--- Stacktrace: ${
                                  Log.getStackTraceString(e)
                              }"
                          )

                          val msg = e.message!!

                          ErrorScreen("Loading $TAG failed.", msg)
                      }
                  }


                 */

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(Modifier.padding(16.dp)) {
                        /*    Text("Please select your server.")

                            for(i in 1..3){
                                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable {

                                }.padding(10.dp)){
                                    Text("Stable")
                                    Text("- 1234")
                                }
                                */


                        val scope = rememberCoroutineScope()


                      //might launch it here already?
                        var textt by remember{
                            mutableStateOf("--- OUTPUT ---")
                        }


                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()

                                }
                                .padding(10.dp)) {
                            Text("Stable >>>")
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {

                                    scope.launch {

                                        val client = ktorHttpClient



                                      val token =      client.post<Map<String,String>>("/login"){
                                            contentType(ContentType.Application.Json)
                                            body= User("fhuuu","1234")
                                        }["token"]

                                        Log.d(TAG, "-------------afef\n\n TOKEN::: $token")

                                        textt += "\n\nPOSTED >> TOKEN: $token"


                                     val ss =   client.get<String>("/hello") {
                                         header(HttpHeaders.Authorization, "Bearer $token")
                                     }

                                      Log.d(TAG, "_----------------\n\nSTRING $ss")

                                        textt += "\n\nAUTHORIZED >> RESPONSE: $ss"



                                    }

                                }
                                .padding(10.dp)) {
                            Text("Stable -- POST")

                        }

                        Text(textt)

                    }
                }
            }

        }
    }
}

@Serializable
data class User(val username: String, val password: String)