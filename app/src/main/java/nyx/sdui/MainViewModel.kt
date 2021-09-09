package nyx.sdui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import nyx.sdui.model.UserLogin
import nyx.sdui.network.Repository

class MainViewModel : ViewModel() {

    private val TAG = "MainViewModel"

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
        Repository.getContent(route)
    } catch (e: Exception) {
        Log.e(TAG, e.message!!)
        e
    }


    //TODO should be made differently anyway
    suspend fun performClick(route: String) {
        Repository.performClick(route, emptyMap())
    }
}