package nyx.sdui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
import kotlinx.coroutines.DelicateCoroutinesApi
import nyx.felix.screens.ErrorScreen
import nyx.sdui.components.base.Component
import nyx.sdui.components.base.ComponentType.*
import nyx.sdui.screens.LoadingScreen
import nyx.sdui.ui.theme.SduiTheme
import nyx.sdui.Status.*


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SduiTheme {
                when (val result = viewModel.result.collectAsState().value) {
                    is Loading -> {
                        Log.w(TAG, "Loading")
                        LoadingScreen()
                    }

                    is Success -> {
                        Log.w(TAG, "Success")
                        ResolveComponent(result.data as Component)
                    }

                    is Failure -> {
                        val e = result.exception as Exception

                        Log.e(
                            TAG,
                            "Loading $TAG failed: ${e.message}\n\n--- Stacktrace: ${
                                Log.getStackTraceString(e)
                            }"
                        )

                        ErrorScreen("Loading $TAG failed.", e.message!!)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ResolveComponent(component: Component) {

        //TODO What about having a mutableMap called Data or so where keys are the Components' IDs and Value Any??
        when (component.type) {
            //layouts
            BOX -> box(component.children!!)
            VERTICAL -> column(component.children!!)
            SCROLL_VERTICAL -> lazyColumn(component.children!!)

            //widgets
            EDIT_TEXT -> textField(component.id, component.data)
            TEXT -> text(component.data!!)
            IMAGE -> image(component.data!!)
            BUTTON -> textButton(component.data!!) {
                viewModel.performClick(component.id)
            }
            DIVIDER -> Divider(Modifier.fillMaxWidth())
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun text(text: String) = Text(text)

    @SuppressLint("ComposableNaming")
    @Composable
    fun textField(id: String, defaultValue: String?) {
        var text by remember { mutableStateOf(TextFieldValue(defaultValue ?: "")) }
        TextField(
            value = text,
            onValueChange = {
                viewModel.data[id] = it.text
                text = it
            })
    }

    @OptIn(ExperimentalCoilApi::class)
    @SuppressLint("ComposableNaming")
    @Composable
    fun image(url: String) = Image(painter = rememberImagePainter(
        data = url,
        imageLoader = LocalImageLoader.current, builder = {
            crossfade(true)
            placeholder(R.mipmap.ic_launcher_round)
            transformations(CircleCropTransformation())
        }
    ), contentDescription = null,
        Modifier
            .fillMaxWidth()
            .height(100.dp))

    @SuppressLint("ComposableNaming")
    @Composable
    fun textButton(text: String, onClick: () -> Unit) =
        Button(onClick, Modifier.padding(top = 40.dp)) {
            Text(text)
        }

    @SuppressLint("ComposableNaming")
    @Composable
    fun column(children: List<Component>) = Column(Modifier.padding(16.dp)) {
        for (child in children) {
            ResolveComponent(child)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun lazyColumn(children: List<Component>) = LazyColumn(Modifier.padding(16.dp)) {
        for (child in children) {
            item {
                ResolveComponent(child)
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun row(children: List<Component>) = Row(Modifier.padding(16.dp)) {
        for (child in children) {
            ResolveComponent(child)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun lazyRow(children: List<Component>) = LazyRow(Modifier.padding(16.dp)) {
        for (child in children) {
            item {
                ResolveComponent(child)
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun box(children: List<Component>) = Box {
        for (child in children) {
            ResolveComponent(child)
        }
    }
}