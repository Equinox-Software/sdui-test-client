package nyx.sdui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import nyx.sdui.Status.*
import nyx.sdui.network.Repository

class MainViewModel : ViewModel() {

    private val TAG = "MainViewModel"
    val result = MutableStateFlow<Status<Any>>(Loading())



    init {
        fetchContent()
    }

    private fun fetchContent() {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = Loading()
            try {
                result.value = Success(Repository.getContent())
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = Failure(e)
            }
        }
    }

    fun <T> performClick(id: String, data:Map<String, T>) {
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