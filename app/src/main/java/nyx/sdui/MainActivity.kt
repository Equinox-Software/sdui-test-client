package nyx.sdui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.LoadPainterDefaults
import kotlinx.serialization.ExperimentalSerializationApi
import nyx.felix.screens.ErrorScreen
import nyx.sdui.model.Component
import nyx.sdui.model.LayoutType
import nyx.sdui.model.WidgetType
import nyx.sdui.screens.LoadingScreen
import nyx.sdui.ui.theme.SduiTheme


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SduiTheme {
                when (val result = viewModel.result.collectAsState().value) {
                    is Status.Loading -> {
                        Log.w(TAG, "Loading")
                        LoadingScreen()
                    }

                    is Status.Success -> {
                        Log.w(TAG, "Success")
                        Content(result.data as Component.Layout)
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


            }
        }
    }


    @OptIn(ExperimentalSerializationApi::class)
    @Composable
    fun Content(response: Component.Layout) {


        ResolveLayout(response.children)


    }


    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ResolveLayout(components: List<Component>) {


        for (component in components) {

            Log.d(TAG, "-- ${component.id} --")

            when (component) {

                is Component.Layout -> {
                    when (component.type) {
                        LayoutType.BOX -> {
                            Box {
                                ResolveLayout(component.children)
                            }
                        }

                        LayoutType.SCROLL_VERTICAL -> {
                            Column(
                                Modifier.padding(
                                    16.dp
                                )
                            ) {
                                 ResolveLayout(component.children)


                            }
                        }

                    }
                }

                is Component.Widget -> {
                    when (component.type) {
                        WidgetType.TEXT -> {
                            Text(component.data)
                        }

                        WidgetType.BUTTON -> {
                            Button({

                                viewModel.performClick(component.id)
                            },Modifier.padding(top=40.dp)) {
                                Text(component.data)
                            }
                        }

                        WidgetType.IMAGE -> {
                            Image(painter = rememberImagePainter(
                                data = component.data,
                                imageLoader = LocalImageLoader.current,
                                builder = {
                                    placeholder(R.drawable.ic_launcher_background)
                                }
                            ), contentDescription ="ef" )
                        }
                    }
                }


            }
        }


    }


}




