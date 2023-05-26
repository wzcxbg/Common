package com.sliver.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@OptIn(FlowPreview::class)
open class BaseViewModel<
        S : BaseViewModel.State,
        E : BaseViewModel.Event,
        I : BaseViewModel.Intent> : ViewModel() {
    private val _state = MutableStateFlow(newState())
    private val _event = MutableSharedFlow<E>(1)
    private val _intent = MutableSharedFlow<I>(1)
    val state: StateFlow<S> = _state.asStateFlow()
    val event: SharedFlow<E> = _event.asSharedFlow()
    protected val intent: SharedFlow<I> = _intent.asSharedFlow()

    init {
        intent
            .flatMapMerge { onCollect(flowOf(it)) }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun tryEmit(value: S) = _state.tryEmit(value)
    fun tryEmit(value: E) = _event.tryEmit(value)
    suspend fun emit(value: S) = _state.emit(value)
    suspend fun emit(value: E) = _event.emit(value)
    suspend fun emitState(value: S) = _state.emit(value)
    suspend fun emitEvent(value: E) = _event.emit(value)
    fun emitIntent(i: I) = _intent.tryEmit(i)

    protected open suspend fun onCollect(flow: Flow<I>): Flow<I> {
        return flow
    }

    protected suspend inline fun <reified Intent : I> Flow<I>.case(crossinline consumer: suspend (Intent) -> Unit): Flow<I> {
        return onEach {
            if (it is Intent) {
                consumer.invoke(it)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun newState(): S {
        val kType = this::class.supertypes[0].arguments[0].type
        val kClass = kType?.classifier as KClass<S>
        val state = kClass.primaryConstructor?.callBy(emptyMap())
        return state as S
    }

    interface IBase
    interface IState : IBase
    interface IEvent : IBase
    open class State : IState
    open class Event : IEvent
    open class Intent
}