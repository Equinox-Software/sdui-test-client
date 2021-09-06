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

    var data = mutableStateOf<MutableMap<String, Any>?>(null)


    init {
        fetchContent()
    }

    private fun fetchContent() {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = Loading()
            try {
                result.value = Success(Repository.getContent())
                data.value = (result.value as Success).page.data.toMutableMap()
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = Failure(e)
            }
        }
    }

    fun performClick(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = Loading()
            try {
                result.value = Success(Repository.performClick(id, data.value!!.toMap()))
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = Failure(e)
            }
        }
    }
}