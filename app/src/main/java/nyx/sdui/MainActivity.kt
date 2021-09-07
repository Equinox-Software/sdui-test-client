package nyx.sdui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import nyx.felix.screens.ErrorScreen
import nyx.sdui.components.base.*
import nyx.sdui.network.ktorHttpClient
import nyx.sdui.screens.LoadingScreen
import nyx.sdui.ui.theme.SduiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    private lateinit var navController: NavHostController

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
                        var textt by remember {
                            mutableStateOf("--- OUTPUT ---")
                        }


                        var routes by remember {
                            mutableStateOf<List<String>?>(null)
                        }

                        scope.launch {
                            delay(2500)
                            routes = listOf("a", "b", "c")
                        }

                        navController = rememberNavController()

                        routes?.let {
                            Log.e("EEEEEEEE", "---\n\n\nNAV CONTROLLER\n\n----")
                            NavHost(navController, startDestination = "b") {
                                it.forEach { route ->
                                    composable(route = route) {
                                        Screen(route = route)
                                    }
                                }
                            }
                        }

                        routes?.let {
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

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {

                                    scope.launch {

                                        val client = ktorHttpClient


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

                        Text(textt)

                    }
                }
            }

        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    fun Screen(route: String) {

        viewModel.fetchContent(route)

        when (val result = viewModel.result.collectAsState().value) {
            is Status.Loading -> {
                Log.w(TAG, "Loading")
                LoadingScreen()
            }

            is Status.Success -> {
                Log.w(TAG, "Success")
                ResolveComponent(result.layout)


            }

            is Status.Failure -> {
                val e = result.exception

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

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun ResolveComponent(component: Component) {

        //TODO What about having a mutableMap called Data or so where keys are the Components' IDs and Value Any??
        when (component.type) {
            //layouts
            ComponentType.BOX -> box(component.children!!)
            ComponentType.VERTICAL -> column(component.children!!)
            ComponentType.SCROLL_VERTICAL -> lazyColumn(component.children!!)
            //     SELECTABLE_LIST -> selectableLazyColumn(component.children!!)
            //   SELECTABLE_ROW -> selectableRow(component.data!! as Map<String,String>)

            //widgets
            ComponentType.EDIT_TEXT -> textField(
                component.id,
                Json.decodeFromJsonElement(component.data!!)
            )
            ComponentType.TEXT -> text(
                Json.decodeFromJsonElement(component.data!!),
                component.style
            )
            ComponentType.IMAGE -> image(Json.decodeFromJsonElement(component.data!!))
            ComponentType.BUTTON -> textButton(
                component.id,
                Json.decodeFromJsonElement(component.data!!),
                component.actions!![ComponentActionType.CLICK]!!
            )
            ComponentType.DIVIDER -> divider()
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
    fun text(text: String, styles: CStyle) {
        ////// better than list? >>>  PaddingValues(0.dp)

        styles.color


        Text(
            text,
            Modifier.padding(
                PaddingValues(
                    styles.padding[0].dp,
                    styles.padding[1].dp,
                    styles.padding[2].dp,
                    styles.padding[3].dp
                )
            ),
            color = Color(styles.color)
        )


    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun textField(id: String, defaultValue: String = "") {
        var text by remember { mutableStateOf(defaultValue) }
        TextField(
            value = text,
            onValueChange = {
                //  viewModel.data.value?.set(id, Json.encodeToJsonElement(it.text))
                text = it
            }, label = {
                Text("what you enter here should appear on the next page (not yet)")
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
            ////   viewModel.performClick(id, mapOf("awad" to "Kuuu"))

            val routes = listOf("a", "b", "c")


            // navController.navigate(routes[routes.indices.random()])

            navController.navigate("a")

            //  resolveAction()
        }) {
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

@Serializable
data class User(val username: String, val password: String)