package nyx.sdui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.JsonElement
import nyx.sdui.model.UserLogin
import nyx.sdui.network.Repository

class MainViewModel : ViewModel() {

    private val TAG = "MainViewModel"

    //might any be better in terms of performance?
    var pageData = mutableStateMapOf<String,Any>()
    var  keys:Set<String>?=null

    fun getUserLogin(context: Context) = Repository.getUserLogin(context)

    suspend fun logIn(user: UserLogin) = try {
        Repository.logIn(user)
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }

    suspend fun signUp(user: UserLogin) = try {
        Repository.signUp(user)
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }

    fun saveToken(token: String) = Repository.saveToken(token)

    suspend fun fetchContent(route: String) = try {
        Log.e(TAG, "-- data:::: ${pageData.toMap()}")
        Repository.getContent(route, keys?.let { keysData -> pageData.filterKeys { keysData.contains(it) } } ?: pageData)
     ////   pageData.clear() should be done somewhere^^
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }


    //TODO should be made differently anyway
    suspend fun performClick(route: String) {
        Repository.performClick(route, emptyMap())
    }
}