package nyx.sdui.network

import io.ktor.client.request.*
import nyx.sdui.model.Component
import nyx.sdui.model.UserEntity

object Repository {

    private val client = ktorHttpClient

    val END_POINT_GET_USER_KTOR = ""
    val END_POINT_POST_USER_KTOR = ""


    suspend fun saveUser(user: UserEntity) {
        client.post<UserEntity>(END_POINT_POST_USER_KTOR) {
            body = user
        }
    }

    suspend fun getContent(): Component = /*TopLayoutResponse("1672222", listOf(
    LayoutResponse("123", LayoutType.TEXT, "Some text goes here"),
    LayoutResponse("12",
    LayoutType.BUTTON, "Click me (fix this part lel)",/*{
            Log.i(
                "TAG",
                "----"
            )
        }*/
    )))*/

        client.get("cont")

    /*     Component.Layout(
             "abc",
             LayoutType.SCROLL_VERTICAL,
             listOf(
                 Component.Widget("aa", WidgetType.TEXT, "Hello!"),
                 Component.Layout(
                     "bb", LayoutType.BOX,
                     listOf(
                         Component.Widget("ab", WidgetType.TEXT, "Heüüüüüülooolo!"),
                         Component.Widget("ba", WidgetType.TEXT, "Hellppo!")
                     )
                 ),
                Component.Widget(
                     "122", WidgetType.BUTTON,
                     "click!!"
                 )
             )
         )
 */


    //make this appear
    suspend fun performClick(id: String, data: Map<String, Any>): Component =/* TopLayoutResponse("12222", listOf(        LayoutResponse("123", LayoutType.TEXT, "Some text goes here"),
        LayoutResponse("1283", LayoutType.TEXT, "ID: $id"),
        LayoutResponse("12",
            LayoutType.BUTTON, "Click me (fix this part lel)",/*{
            Log.i(
                "TAG",
                "----"
            )
        }*/
        ), LayoutResponse("182",LayoutType.TEXT,"yeee")))*/

        /*    Component(
                "abc",
                ComponentType.SCROLL_VERTICAL,null,
                listOf(
                    Component("ab", ComponentType.IMAGE, "https://cdn.wallpapersafari.com/46/29/MTLnRp.jpg"),
                    Component("ba", ComponentType.TEXT, "Helltthppo! --- $id")
                )


            )
    */

        client.get("click${id}") {
            body = data
        }

}