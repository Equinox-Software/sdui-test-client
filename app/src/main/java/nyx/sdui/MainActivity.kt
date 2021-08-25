package nyx.sdui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import nyx.felix.screens.ErrorScreen
import nyx.sdui.model.Component
import nyx.sdui.model.ComponentType
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
                        ResolveComponent(result.data as Component)
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

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ResolveComponent(component: Component) {

        /*
        What about having a mutableMap called Data or so where keys are the Components' IDs and Value Any??
         */

        when (component.type) {
            ComponentType.BOX -> {
                Box {
                    for (child in component.children!!) {
                        ResolveComponent(child)
                    }

                }
            }

            ComponentType.VERTICAL -> {
                Column(
                    Modifier.padding(
                        16.dp
                    )
                ) {


                    for (child in component.children!!) {
                        ResolveComponent(child)
                    }


                }
            }


            ComponentType.EDIT_TEXT -> {

                val texx by remember{
                    mutableStateOf(TextFieldValue(viewModel.data[component.id].toString()))

                }

                TextField(
                    value = texx,
                    onValueChange = {
                        viewModel.data[component.id] = it.text
                    })
            }

            ComponentType.SCROLL_VERTICAL -> {
                LazyColumn(
                    Modifier.padding(
                        16.dp
                    )
                ) {


                    for (child in component.children!!) {
                        item {
                            ResolveComponent(child)
                        }
                    }
                }


            }


            ComponentType.TEXT -> {
                Text(component.data!!)
            }

            ComponentType.IMAGE -> {
                Image(painter = rememberImagePainter(
                    data = component.data,
                    imageLoader = LocalImageLoader.current, builder = {
                        crossfade(true)
                        placeholder(R.mipmap.ic_launcher_round)
                        transformations(CircleCropTransformation())
                    }
                ), contentDescription = null,
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp))
            }

            ComponentType.BUTTON -> {
                Button({

                    viewModel.performClick(component.id)
                }, Modifier.padding(top = 40.dp)) {
                    Text(component.data!!)
                }
            }


        }
    }
}