package nyx.sdui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nyx.sdui.Status.*
import nyx.sdui.network.Repository

class MainViewModel : ViewModel() {

    private val TAG = "MainViewModel"
    val result = MutableStateFlow<Status<Any>>(Loading())

   fun fetchContent(route:String) {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = Loading()
            try {
                result.value = Success(Repository.getContent(route))
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = Failure(e)
            }
        }
    }

    fun performClick(id: String, data: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = Loading()
            try {
                result.value = Success(Repository.performClick(id, data))
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = Failure(e)
            }
        }
    }
}