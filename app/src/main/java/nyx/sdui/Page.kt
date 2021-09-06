package nyx.sdui

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import nyx.sdui.components.base.Component
import nyx.sdui.components.base.ComponentType
import nyx.sdui.screens.LoadingScreen

@Serializable
class PPage(private val name: String) {

    val TAG = "PAGE"
    lateinit var layout: Component

    private val viewModel = PageViewModel(name)

    @Composable
    fun initializePage(pageData: SnapshotStateMap<String, JsonElement> = mutableStateMapOf()) {
        viewModel.data = pageData
        viewModel.fetchContent(name)

        when (val result = viewModel.result.collectAsState().value) {
            LoadingState.LOADING -> {
                Log.w(TAG, "Loading")
                LoadingScreen()
            }

            LoadingState.SUCCESS -> {
                Log.w(TAG, "Success")
                viewModel.layout?.let { ResolveComponent(it) }
            }

            LoadingState.ERROR -> {
                /*   val e = result.exception as Exception

                   Log.e(
                       TAG,
                       "Loading $TAG failed: ${e.message}\n\n--- Stacktrace: ${
                           Log.getStackTraceString(e)
                       }"
                   )

                   ErrorScreen("Loading $TAG failed.", e.message!!)

                 */
            }
        }
    }

    @Composable
    fun ResolveComponent(component: Component) {

        //TODO What about having a mutableMap called Data or so where keys are the Components' IDs and Value Any??
        when (component.type) {
            //layouts
            ComponentType.BOX -> box(component.children!!)
            ComponentType.VERTICAL -> column(component.children!!)
            ComponentType.SCROLL_VERTICAL -> lazyColumn(component.children!!)
            ComponentType.SELECTABLE_LIST -> selectableLazyColumn(component.children!!)
            //     ComponentType.SELECTABLE_ROW -> selectableRow(component.data!!)

            //widgets
            ComponentType.EDIT_TEXT -> textField(component.id, component.data!!)
            ComponentType.TEXT -> text(component.data!!)
            ComponentType.IMAGE -> image(component.data!!)
            //      BUTTON -> textButton(component.data!!["text"].toString(),component.actions!![ComponentActionType.CLICK])
            ComponentType.DIVIDER -> divider()
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
                //      viewModel.data[id] = it.text
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
    fun textButton(text: String, action: JsonElement) =
        Button({
            //  resolveAction()
        }, Modifier.padding(top = 40.dp)) {
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
    fun selectableLazyColumn(children: List<Component>) = LazyColumn(Modifier.padding(16.dp)) {
        for (child in children) {
            item {
                //        selectableRow(child.data!!)
                divider()
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun selectableRow(data: String) {
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
                text(text = data)
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