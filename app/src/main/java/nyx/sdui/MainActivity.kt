package nyx.sdui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import nyx.felix.screens.ErrorScreen
import nyx.sdui.components.base.*
import nyx.sdui.ui.theme.SduiTheme
import nyx.sdui.util.applyStyle
import nyx.sdui.components.base.ComponentType.*
import nyx.sdui.util.paddingValues

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    private lateinit var navController: NavHostController

    //TODO get rid of that via login anyway
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("CoroutineCreationDuringComposition")
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

                LoadableView {
                    val result by loadAsync {
                        viewModel.fetchRoutes()
                    }

                    whenReady {
                        when (result) {
                            is Exception -> ErrorScreen(
                                errorTitle = "Loading Routes failed.",
                                errorMessage = (result as Exception).message!!
                            )
                            is List<*> -> {
                                val routes = result as List<String>
                                Log.d(TAG, "Loaded routes: $routes")

                                navController = rememberNavController()

                                Log.e("EEEEEEEE", "---\n\n\nNAV CONTROLLER\n\n----")
                                NavHost(navController, startDestination = "b") {
                                    routes.forEach { route ->
                                        composable(route = route) {
                                            Screen(route = route)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                /*    routes?.let {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {


                                    navController.navigate("c")

                                }
                                .padding(10.dp)) {
                            Text("Stable >>>")
                        }
                    }
                 */

                /*
                Will be useful for login
Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {

                            scope.launch {

                                val token = client.post<Map<String, String>>("/login") {
                                    contentType(ContentType.Application.Json)
                                    body = User("fhuuu", "1234")
                                }["token"]

                                Log.d(TAG, "-------------afef\n\n TOKEN::: $token")

                                textt += "\n\nPOSTED >> TOKEN: $token"


                                val ss = client.get<String>("/hello") {
                                    header(HttpHeaders.Authorization, "Bearer $token")
                                }

                                Log.d(TAG, "_----------------\n\nSTRING $ss")

                                textt += "\n\nAUTHORIZED >> RESPONSE: $ss"


                            }

                        }
                        .padding(10.dp)) {
                    Text("Stable -- POST")

                }

                Text(textt)}}
*/
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun Screen(route: String) = LoadableView {
        val result by loadAsync {
            viewModel.fetchContent(route)
        }

        whenReady {
            when (result) {
                is Exception -> ErrorScreen(
                    errorTitle = "Loading Page $route failed.",
                    errorMessage = (result as Exception).message!!
                )
                is Component -> ResolveComponent(result as Component)
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ResolveComponent(component: Component) {

        //TODO What about having a mutableMap called Data or so where keys are the Components' IDs and Value Any??
        when (component.type) {
            //layouts
            BOX -> box(component.children!!,component.style)
            VERTICAL -> column(component.children!!,component.style)
            SCROLL_VERTICAL -> lazyColumn(component.children!!,component.style)
            HORIZONTAL -> row(component.children!!,component.style)
            SCROLL_HORIZONTAL -> lazyRow(component.children!!,component.style)
            //     SELECTABLE_LIST -> selectableLazyColumn(component.children!!)
            //   SELECTABLE_ROW -> selectableRow(component.data!! as Map<String,String>)

            //widgets
            EDIT_TEXT -> textField(
                component.id,
                Json.decodeFromJsonElement(component.data!!),component.style
            )
            TEXT -> text(
                Json.decodeFromJsonElement(component.data!!),
                component.style
            )
            IMAGE -> image(Json.decodeFromJsonElement(component.data!!),component.style)
            BUTTON -> textButton(
                component.id,
                Json.decodeFromJsonElement(component.data!!),
                component.action!!
            )
            DIVIDER -> divider(component.style)
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
    fun text(text: String, style: CStyle) {
        ////// better than list? >>>  PaddingValues(0.dp)

        Text(
            text,
            Modifier.applyStyle(style),
            color = Color(style.color)
        )


    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun textField(id: String, defaultValue: String = "",style: CStyle?) {
        var text by remember { mutableStateOf(defaultValue) }
        TextField(
            value = text,

            onValueChange = {
                //  viewModel.data.value?.set(id, Json.encodeToJsonElement(it.text))
                text = it
            },
            Modifier.applyStyle(style),
            label = {
                Text("what you enter here should appear on the next page (not yet)")
            })
    }

    @OptIn(ExperimentalCoilApi::class)
    @SuppressLint("ComposableNaming")
    @Composable
    fun image(url: String,style: CStyle?) = Image(painter = rememberImagePainter(
        data = url,
        imageLoader = LocalImageLoader.current, builder = {
            crossfade(true)
            placeholder(R.mipmap.ic_launcher_round)
            transformations(CircleCropTransformation())
        }
    ), contentDescription = null,
        Modifier.applyStyle(style))

    @SuppressLint("ComposableNaming")
    @Composable
    fun textButton(id: String, text: String, action: CAction) = Button({
        ////   viewModel.performClick(id, mapOf("awad" to "Kuuu"))

        val routes = listOf("a", "b", "c")

        action.click?.let {
//what to do here??

            Toast.makeText(this, "Click --- $it", Toast.LENGTH_LONG).show()
        }

        action.navigate?.let {
            navController.navigate(it)
        }


        //    navController.navigate("a")

        //  resolveAction()
    }) {
        Text(text)
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun column(children: List<Component>,style: CStyle?) = Column(Modifier.applyStyle(style)) {
        for (child in children) {
            ResolveComponent(child)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun lazyColumn(children: List<Component>,style: CStyle?) = LazyColumn(Modifier.applyStyle(style)) {
        for (child in children) {
            item {
                ResolveComponent(child)
            }
        }
    }

/*    @SuppressLint("ComposableNaming")
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
                .selectable {
                    selected = !selected
                }) {
            if (selected) {
                text("SELECTED")
            } else {
                text(text = data["Text"].toString())
            }

        }
    }

 */

    @SuppressLint("ComposableNaming")
    @Composable
    fun row(children: List<Component>,style: CStyle?) = Row(Modifier.applyStyle(style)) {
        for (child in children) {
            ResolveComponent(child)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun lazyRow(children: List<Component>,style: CStyle?) = LazyRow(Modifier.applyStyle(style)) {
        for (child in children) {
            item {
                ResolveComponent(child)
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun box(children: List<Component>,style: CStyle?) = Box(Modifier.applyStyle(style)) {
        for (child in children) {
            ResolveComponent(child)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun divider(style: CStyle?) = Divider(Modifier.applyStyle(style))


}

@Serializable
data class User(val username: String, val password: String)