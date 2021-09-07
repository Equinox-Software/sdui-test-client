package nyx.sdui

import android.util.Log
import androidx.lifecycle.ViewModel
import nyx.sdui.network.Repository

class MainViewModel : ViewModel() {

    private val TAG = "MainViewModel"

    suspend fun fetchContent(route: String) = try {
        Repository.getContent(route)
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }

    suspend fun fetchRoutes() = try {
        Repository.getRoutes()
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }

    //TODO should be made differently anyway
    suspend fun performClick(route: String) {
        Repository.performClick(route, emptyMap())
    }
}