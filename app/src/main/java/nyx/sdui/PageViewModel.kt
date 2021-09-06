package nyx.sdui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import nyx.sdui.components.base.Component

@Serializable
class PageViewModel(private val pageName: String) : ViewModel() {

    private val TAG = pageName + "ViewModel"

    @Contextual
    var layout by mutableStateOf<Component?>(null)

    @Contextual
    val result = MutableStateFlow(LoadingState.LOADING)

    @Contextual
    var data = mutableStateMapOf<String, JsonElement>()

    init {
        fetchContent(pageName)
    }

    fun fetchContent(pageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            result.value = LoadingState.LOADING
            try {
                //  layout = Repository.getContent()

                result.value = LoadingState.SUCCESS

            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                result.value = LoadingState.ERROR
            }
        }
    }

    /* fun performClick(id: String) {
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

     */
}

enum class LoadingState {
    LOADING, SUCCESS, ERROR
}