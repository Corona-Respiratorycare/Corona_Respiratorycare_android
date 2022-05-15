package com.covidproject.covid_respiratorycare.ui.map

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

// out T는 읽기, 리턴만 할 수 있다.
open class MapEvent<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) { // 이벤트가 이미 처리 되었다면
            null // null을 반환하고,
        } else { // 그렇지 않다면
            hasBeenHandled = true // 이벤트가 처리되었다고 표시한 후에
            content // 값을 반환합니다.
        }
    }

    // 이벤트의 처리 여부에 상관 없이 값을 반환
    fun peekContent(): T = content
}

@MainThread
inline fun <T> LiveData<MapEvent<T>>.eventObserve(
    owner: LifecycleOwner,
    // crossinline으로 내부에서 함수로 오는 파라미터를 쓸 수 있다.
    crossinline onChanged: (T) -> Unit
): Observer<MapEvent<T>> {
    val wrappedObserver = Observer<MapEvent<T>> { t ->
        // t.getContent.. 가 null 이 아닐때 실행됨
        t.getContentIfNotHandled()?.let {
            // 이름 없이 호출되는 함수 invoke
            onChanged.invoke(it)
        }
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}