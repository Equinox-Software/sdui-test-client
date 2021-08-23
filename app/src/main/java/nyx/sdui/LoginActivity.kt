package nyx.sdui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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


                        scope.launch {

                            val client = ktorHttpClient

                            //    client.post("login")
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

                                }
                                .padding(10.dp)) {
                            Text("Stable")

                        }

                    }
                }
            }

        }
    }
}


