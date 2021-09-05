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
import androidx.compose.foundation.selection.selectable
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
import kotlinx.serialization.json.JsonElement
import nyx.felix.screens.ErrorScreen
import nyx.sdui.Status.*
import nyx.sdui.components.base.Component
import nyx.sdui.components.base.ComponentAction
import nyx.sdui.components.base.ComponentActionType
import nyx.sdui.components.base.ComponentType.*
import nyx.sdui.screens.LoadingScreen
import nyx.sdui.ui.theme.SduiTheme


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
            SELECTABLE_LIST -> selectableLazyColumn(component.children!!)
            SELECTABLE_ROW -> selectableRow(component.data!! as Map<String,String>)

            //widgets
            EDIT_TEXT -> textField(component.id, component.data!!.toString())
            TEXT -> text(component.data!!.toString())
            IMAGE -> image(component.data!!.toString())
            BUTTON -> textButton(
                component.id,
                component.data!!.toString(),
                component.actions!![ComponentActionType.CLICK]!!
            )
            DIVIDER -> divider()
        }
    }

    @Composable
    fun ResolveAction(actionType: ComponentActionType, action: Any) {

        when (actionType) {
            ComponentActionType.CLICK -> {
                when (action) {
                    ComponentAction.OPEN_PAGE -> {
                    }
                }
            }
            ComponentActionType.SELECT -> {

            }
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
    fun textButton(id: String, text: String, action: JsonElement) =
        Button({
            viewModel.performClick(id)
            //  resolveAction()
        }, Modifier.padding(top = 40.dp)) {
            Text(text)
        }

    @SuppressLint("ComposableNaming")
    @Composable
    fun column(children: List<Component>) = Column(Modifier.padding(16.dp)) {
        for (child in children) {
            ResolveComponent(child)

            var amir by remember {
                mutableStateOf("lavat")
            }
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
    fun selectableLazyColumn(children: List<Component>) = LazyColumn(Modifier.padding(16.dp)) {
        for (child in children) {
            item {
                selectableRow(child.data!! as Map<String,String>)
                divider()
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun selectableRow(data: Map<String, String>) {
        var selected by remember {
            mutableStateOf(false)
        }

        Row(
            Modifier
                .padding(10.dp)
                .selectable(selected) {
                    selected = !selected
                }) {
            if (selected) {
                text("SELECTED")
            } else {
                text(text = data["Text"].toString())
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

    @SuppressLint("ComposableNaming")
    @Composable
    fun divider() = Divider(Modifier.fillMaxWidth())


}



