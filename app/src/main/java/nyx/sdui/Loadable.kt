package nyx.sdui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.*
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.random.Random
import kotlin.reflect.KProperty


@OptIn(ExperimentalCoroutinesApi::class)
private class DeferredDelegate<T>(val deferred: Deferred<T>) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return deferred.getCompleted()
    }
}


@DelicateCoroutinesApi
interface SuspendingLoadableViewScope {
    val readyStatesState: SnapshotStateMap<StateKey, Pair<State<Boolean>, Deferred<Any?>>>

    @Composable
    @DelicateCoroutinesApi
    fun <T> loadAsync(
        block: suspend CoroutineScope.() -> T
    ): PropertyDelegateProvider<Nothing?, ReadOnlyProperty<Any?, T>>

    @SuppressLint("ComposableNaming")
    @Composable
    fun waitFor(ready: Boolean)

    fun waitFor(ready: State<Boolean>)

    @SuppressLint("ComposableNaming")
    @Composable
    fun whenReady(block: @Composable SuspendingLoadableViewScope.() -> Unit)
}


@OptIn(DelicateCoroutinesApi::class)
private class SuspendingLoadableViewScopeWithoutSpinner(other: SuspendingLoadableViewScope) :
    SuspendingLoadableViewScope by other {
    @SuppressLint("ComposableNaming")
    @Composable
    override fun whenReady(block: @Composable SuspendingLoadableViewScope.() -> Unit) {
        val ready by remember(*readyStatesState.values.toTypedArray()) {
            derivedStateOf { readyStatesState.values.all { it.first.value } }
        }
        if (ready) {
            block()
        }
    }
}


sealed class StateKey {
    data class StringKey(private val string: String) : StateKey()
    class IdentityKey : StateKey() {
        override fun equals(other: Any?): Boolean {
            return this === other
        }

        private val hashCode by lazy { Random.nextInt() }
        override fun hashCode() = hashCode
    }
}


@OptIn(DelicateCoroutinesApi::class)
private class SuspendingLoadableViewScopeImpl(
    val scope: CoroutineScope,
    override val readyStatesState: SnapshotStateMap<StateKey, Pair<State<Boolean>, Deferred<Any?>>>
) : SuspendingLoadableViewScope {
    private class LoadAsyncDelegateProvider<T>(
        val scope: SuspendingLoadableViewScopeImpl,
        val block: suspend CoroutineScope.() -> T
    ) : PropertyDelegateProvider<Nothing?, DeferredDelegate<T>> {
        override fun provideDelegate(
            thisRef: Nothing?,
            property: KProperty<*>
        ): DeferredDelegate<T> {
            val state = mutableStateOf(false)
            val key = StateKey.StringKey(property.name)
            scope.readyStatesState[key]?.let {
                @Suppress("UNCHECKED_CAST")
                return DeferredDelegate(it.second as Deferred<T>)
            }
            val deferred = scope.scope.async {
                try {
                    block()
                } finally {
                    state.value = true
                }
            }
            scope.readyStatesState[key] = state to deferred

            return DeferredDelegate(deferred)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    override fun <T> loadAsync(
        block: suspend CoroutineScope.() -> T
    ): PropertyDelegateProvider<Nothing?, ReadOnlyProperty<Any?, T>> =
        LoadAsyncDelegateProvider(this, block)

    @SuppressLint("ComposableNaming")
    @Composable
    @NonRestartableComposable
    override fun waitFor(ready: Boolean) {
        val state = remember { mutableStateOf(ready) }
        state.value = ready
        waitFor(state)
    }

    override fun waitFor(ready: State<Boolean>) {
        readyStatesState[StateKey.IdentityKey()] = ready to CompletableDeferred(value = null)
    }

    @SuppressLint("ComposableNaming")
    @Composable
    override fun whenReady(block: @Composable SuspendingLoadableViewScope.() -> Unit) {
        val ready by remember(*readyStatesState.values.toTypedArray()) {
            derivedStateOf { readyStatesState.values.all { it.first.value } }
        }
        LoadableView(ready) { block(SuspendingLoadableViewScopeWithoutSpinner(this)) }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@NonRestartableComposable
@Composable
fun LoadableView(
    block: @Composable SuspendingLoadableViewScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewScope = remember { SuspendingLoadableViewScopeImpl(scope, mutableStateMapOf()) }
    block(viewScope)
}

@Composable
private fun LoadableView(
    ready: Boolean,
    block: @Composable () -> Unit
) {
    if (ready) {
        block()
    }
    if (!ready) { // we may no longer be ready
        Box(
            Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}